package net.quillcraft.lumy.server.manager;

import net.quillcraft.lumy.server.data.RedisManager;
import net.quillcraft.lumy.server.text.Text;
import net.quillcraft.lumy.server.text.TextList;
import net.quillcraft.lumy.utils.FileConfiguration;
import net.quillcraft.lumy.utils.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public enum LanguageManager{

    ENGLISH_US("en_us"),
    FRENCH("fr_fr");

    private final String iso;

    private final static RedissonClient redissonClient = RedisManager.TEXT.getRedisAccess().getRedissonClient();
    private final static Logger logger = LogManager.getLogger("Lumy");

    LanguageManager(final String isoLanguage){
        this.iso = isoLanguage;
    }

    public static void initAllLanguage(){
        logger.info("Language Manager Init");
        Arrays.stream(values()).parallel().forEach(LanguageManager::updateTexteRedis);
    }

    public static List<LanguageManager> getLastLanguagesModifiedTime(int time, TimeUnit timeUnit){

        Arrays.stream(values()).forEach(languageManager -> System.out.println(getFileLastModifiedTime(languageManager)+" / "+timeUnit.toHours(time)));

        return Arrays.stream(values()).parallel().filter(languageManager -> getFileLastModifiedTime(languageManager) <= timeUnit.toHours(time)).toList();
    }

    private static Long getFileLastModifiedTime(LanguageManager languageManager){
        final File file = FileUtils.getFileFromResource("languages/"+languageManager.getISO()+".yml");
        return Objects.requireNonNull(file).lastModified();
    }

    public void updateTexteRedis(){
        logger.info("Language Manager Update ISO ("+iso.toUpperCase()+")");
        redissonClient.getKeys().deleteByPattern(iso+":*");

        final FileConfiguration textFile = new FileConfiguration(FileUtils.getFileInputStreamFromResource("languages/"+iso+".yml"));

        try{
            Arrays.stream(Text.values()).parallel().forEach(text -> {
                final String path = text.getPath();
                redissonClient.getBucket(iso+":"+path).set(textFile.getString(path));
            });

            Arrays.stream(TextList.values()).parallel().forEach(text -> {
                final String path = text.getPath();
                final RList<String> textList = redissonClient.getList(iso+":"+path);
                textList.clear();
                textList.addAll(textFile.getStringList(path));
            });
        }catch(Exception exception){
            logger.error("Error text init ("+iso.toUpperCase()+")", exception);
        }
    }

    public String getISO(){
        return iso;
    }

}
