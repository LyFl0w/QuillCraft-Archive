package net.quillcraft.lumy.server.manager;

import net.quillcraft.lumy.Lumy;
import net.quillcraft.lumy.api.text.Text;
import net.quillcraft.lumy.api.text.TextBase;
import net.quillcraft.lumy.api.text.TextList;
import net.quillcraft.lumy.api.utils.FileUtils;
import net.quillcraft.lumy.server.data.RedisManager;
import net.quillcraft.lumy.utils.FileConfiguration;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

public enum LanguageManager {

    ENGLISH_US("en_us"),
    FRENCH("fr_fr"),
    DEFAULT(ENGLISH_US.getISO());

    private final String iso;

    private static final RedissonClient redissonClient = RedisManager.TEXT.getRedisAccess().getRedissonClient();
    private static final Logger logger = Lumy.logger;
    private static final Map<LanguageManager, Long> lastUpdate = new EnumMap<>(LanguageManager.class);

    LanguageManager(final String isoLanguage) {
        this.iso = isoLanguage;
    }

    public static List<LanguageManager> getLastLanguagesModifiedTime(int time, TimeUnit timeUnit) {
        final long timeToWait = System.currentTimeMillis() - timeUnit.toMillis(time);

        if (lastUpdate.isEmpty()) {
            final List<LanguageManager> languages = Arrays.stream(values()).filter(languageManager -> languageManager != LanguageManager.DEFAULT).toList();
            languages.stream().parallel().forEach(language -> lastUpdate.put(language, language.getFileLastModifiedTime()));
            return languages.stream().parallel().filter(language -> language.getFileLastModifiedTime() >= timeToWait).toList();
        }

        final List<LanguageManager> languages = new ArrayList<>();

        lastUpdate.forEach((language, lastUpdateFile) -> {
            if (!lastUpdateFile.equals(language.getFileLastModifiedTime())) {
                languages.add(language);
                lastUpdate.replace(language, language.getFileLastModifiedTime());
            }
        });

        return languages.stream().parallel().filter(language -> language.getFileLastModifiedTime() >= timeToWait).toList();
    }

    private Long getFileLastModifiedTime() {
        final File file = FileUtils.getFileFromResource("languages/" + iso + ".yml");
        return file.lastModified();
    }

    public void updateTexteRedis() {
        logger.info("Language Manager Update ISO {}", iso.toUpperCase());

        try {
            final FileConfiguration textFile =
                    new FileConfiguration(new FileInputStream(FileUtils.getFileFromResource("languages/" + iso + ".yml")));

            final Stream<Text> textStream = Arrays.stream(Text.values()).parallel();
            final Stream<TextList> textListStream = Arrays.stream(TextList.values()).parallel();

            redissonClient.getKeys().deleteByPattern(iso + ":*");

            if (DEFAULT.getISO().equals(iso)) {
                textStream.forEach(consumerText(textFile));
                textListStream.forEach(consumerTextList(textFile));
            } else {
                textStream.filter(TextBase::isNotDefaultText).forEach(consumerText(textFile));
                textListStream.filter(TextBase::isNotDefaultText).forEach(consumerTextList(textFile));
            }

        } catch (Exception exception) {
            logger.error("Error text init ({})", iso.toUpperCase());
            logger.error(exception);
        }
    }

    private Consumer<Text> consumerText(FileConfiguration textFile) {
        return text -> {
            final String path = text.getPath();
            redissonClient.getBucket(iso + ":" + path).set(textFile.getString(path));
        };
    }

    private Consumer<TextList> consumerTextList(FileConfiguration textFile) {
        return text -> {
            final String path = text.getPath();
            final RList<String> textList = redissonClient.getList(iso + ":" + path);
            textList.clear();
            textList.addAll(textFile.getStringList(path));
        };
    }

    public String getISO() {
        return iso;
    }

}
