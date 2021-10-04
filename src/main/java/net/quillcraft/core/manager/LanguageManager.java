package net.quillcraft.core.manager;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.text.Text;
import net.quillcraft.core.text.TextList;
import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.List;

public enum LanguageManager {

    ENGLISH_US("en_us"),
    FRENCH("fr_fr");

    private final String iso;

    private final static RedissonClient redissonClient = RedisManager.TEXT.getRedisAccess().getRedissonClient();

    LanguageManager(final String isoLanguage){
        this.iso = isoLanguage;
    }

    public static void initAllLanguage(JavaPlugin javaPlugin){
        redissonClient.getKeys().deleteByPattern("*core*");

        Arrays.stream(values()).parallel().forEach(languageManager -> {
            final String languageISO = languageManager.getISO();
            final FileConfiguration textFile = new YamlConfigurationBuilder(javaPlugin,"languages/"+languageISO+".yml", true).getConfig();

            Arrays.stream(Text.values()).parallel().filter(text -> textFile.contains(text.getPath())).forEach(text -> {
                final String path = text.getPath();
                redissonClient.getBucket(languageISO+":"+path).set(textFile.getString(path));
            });

            Arrays.stream(TextList.values()).parallel().filter(text -> textFile.contains(text.getPath())).forEach(text -> {
                final String path = text.getPath();
                final RList<String> textList = redissonClient.getList(languageISO+":"+path);
                textList.clear();
                textList.addAll(textFile.getStringList(path));
            });
        });
    }

    public static LanguageManager getLanguageByISO(final String isoLanguage){
        return Arrays.stream(values()).parallel().filter(textManager -> textManager.getISO().equalsIgnoreCase(isoLanguage)).findFirst().orElse(LanguageManager.ENGLISH_US);
    }

    public static LanguageManager getLanguage(final Player player){
        try{
            return getLanguage(new AccountProvider(player).getAccount());
        }catch(AccountNotFoundException e){
            e.printStackTrace();
        }
        return LanguageManager.ENGLISH_US;
    }

    public static LanguageManager getLanguage(final Account account){
        return getLanguageByISO(account.getLanguageISO());
    }

    public static LanguageManager getPlayerLocaleLanguage(final Player player){
        return LanguageManager.getLanguageByISO(player.getLocale());
    }

    public static String getMessageByDefaultLanguage(final Text text){
        return ENGLISH_US.getMessage(text);
    }

    public static List<String> getListMessageByDefaultLanguage(final TextList textList){
        return ENGLISH_US.getMessage(textList);
    }

    public String getMessage(final Text text){
        final RBucket<String> message = redissonClient.getBucket(getISO()+":"+text.getPath());
        return message.get();
    }

    public List<String> getMessage(final TextList text){
        final RList<String> message = redissonClient.getList(getISO()+":"+text.getPath());
        return message.readAll();
    }

    public String getISO(){
        return iso;
    }

}
