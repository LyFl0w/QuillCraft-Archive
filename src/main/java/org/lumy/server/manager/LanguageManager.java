package org.lumy.server.manager;

import org.lumy.Lumy;
import org.lumy.api.text.Text;
import org.lumy.api.text.TextBase;
import org.lumy.api.text.TextList;
import org.lumy.api.utils.FileUtils;
import org.lumy.server.data.RedisManager;
import org.lumy.utils.FileConfiguration;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

public enum LanguageManager{

    ENGLISH_US("en_us"),
    FRENCH("fr_fr"),
    DEFAULT(ENGLISH_US.getISO());

    private final String iso;

    private final static RedissonClient redissonClient = RedisManager.TEXT.getRedisAccess().getRedissonClient();
    private final static Logger logger = Lumy.logger;
    private final static HashMap<LanguageManager, Long> lastUpdate = new HashMap<>();

    LanguageManager(final String isoLanguage){
        this.iso = isoLanguage;
    }

    public static List<LanguageManager> getLastLanguagesModifiedTime(int time, TimeUnit timeUnit){
        final long timeToWait = System.currentTimeMillis()-timeUnit.toMillis(time);

        if(lastUpdate.isEmpty()){
            final List<LanguageManager> languages = getLanguages();
            languages.stream().parallel().forEach(language -> lastUpdate.put(language, language.getFileLastModifiedTime()));
            return languages.stream().parallel().filter(language -> language.getFileLastModifiedTime() >= timeToWait).toList();
        }

        final List<LanguageManager> languages = new ArrayList<>();

        lastUpdate.forEach((language, lastUpdateFile) -> {
            if(!lastUpdateFile.equals(language.getFileLastModifiedTime())){
                languages.add(language);
                lastUpdate.replace(language, language.getFileLastModifiedTime());
            }
        });

        return languages.stream().parallel().filter(language -> language.getFileLastModifiedTime() >= timeToWait).toList();
    }

    public static List<LanguageManager> getLanguages(){
        return Arrays.stream(values()).filter(languageManager -> languageManager != LanguageManager.DEFAULT).toList();
    }

    private Long getFileLastModifiedTime(){
        final File file = FileUtils.getFileFromResource("languages/"+iso+".yml");
        return file.lastModified();
    }

    public void updateTexteRedis(){
        logger.info("Language Manager Update ISO ("+iso.toUpperCase()+")");

        try{
            final FileConfiguration textFile =
                    new FileConfiguration(new FileInputStream(FileUtils.getFileFromResource("languages/"+iso+".yml")));

            final Stream<Text> textStream = Arrays.stream(Text.values()).parallel();
            final Stream<TextList> textListStream = Arrays.stream(TextList.values()).parallel();

            redissonClient.getKeys().deleteByPattern(iso+":*");

            if(DEFAULT.getISO().equals(iso)){
                textStream.forEach(consumerText(textFile));
                textListStream.forEach(consumerTextList(textFile));
            }else{
                textStream.filter(TextBase::isNotDefaultText).forEach(consumerText(textFile));
                textListStream.filter(TextBase::isNotDefaultText).forEach(consumerTextList(textFile));
            }

        }catch(Exception exception){
            logger.error("Error text init ("+iso.toUpperCase()+")", exception);
        }
    }

    private Consumer<Text> consumerText(FileConfiguration textFile){
        return text -> {
            final String path = text.getPath();
            redissonClient.getBucket(iso+":"+path).set(textFile.getString(path));
        };
    }

    private Consumer<TextList> consumerTextList(FileConfiguration textFile){
        return text -> {
            final String path = text.getPath();
            final RList<String> textList = redissonClient.getList(iso+":"+path);
            textList.clear();
            textList.addAll(textFile.getStringList(path));
        };
    }

    public String getISO(){
        return iso;
    }

}
