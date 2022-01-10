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
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum LanguageManager{

    ENGLISH_US("en_us"),
    FRENCH("fr_fr"),
    DEFAULT(ENGLISH_US.getISO());

    private final String iso;

    private final static RedissonClient redissonClient = RedisManager.TEXT.getRedisAccess().getRedissonClient();
    private final static Logger logger = Lumy.logger;

    LanguageManager(final String isoLanguage){
        this.iso = isoLanguage;
    }

    public static void initAllLanguage(){
        logger.info("Language Manager Init");
        Arrays.stream(values()).parallel().filter(language -> language != DEFAULT).forEach(LanguageManager::updateTexteRedis);
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

        try{
            final FileConfiguration textFile = new FileConfiguration(new FileInputStream(FileUtils.getFileFromResource("languages/"+iso+".yml")));

            final Stream<Text> textStream = Arrays.stream(Text.values()).parallel();
            final Stream<TextList> textListStream = Arrays.stream(TextList.values()).parallel();

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
            final String result = textFile.getString(path);
            if(result != null) redissonClient.getBucket(iso+":"+path).set(result);
        };
    }

    private Consumer<TextList> consumerTextList(FileConfiguration textFile){
        return text -> {
            final String path = text.getPath();
            final Collection<String> result = textFile.getStringList(path);
            if(result != null){
                final RList<String> textList = redissonClient.getList(iso+":"+path);
                textList.clear();
                textList.addAll(result);
            }else{
                logger.error("result null !");
            }
        };
    }

    public String getISO(){
        return iso;
    }

}
