package net.quillcraft.core.manager;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.data.management.redis.RedisManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.lumy.api.text.Text;
import org.lumy.api.text.TextList;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.List;

public enum LanguageManager {

    ENGLISH_US("en_us"),
    FRENCH("fr_fr"),

    DEFAULT(ENGLISH_US.getISO());

    private final String iso;

    private final static RedissonClient redissonClient = RedisManager.TEXT.getRedisAccess().getRedissonClient();

    LanguageManager(final String isoLanguage){
        this.iso = isoLanguage;
    }

    public static LanguageManager getLanguageByISO(final String isoLanguage){
        return Arrays.stream(values()).parallel().filter(textManager -> textManager.getISO().equalsIgnoreCase(isoLanguage)).findFirst().orElse(LanguageManager.DEFAULT);
    }

    public static LanguageManager getLanguage(final Player player){
        try{
            return getLanguage(new AccountProvider(player).getAccount());
        }catch(AccountNotFoundException exception){
            Bukkit.getLogger().severe(exception.getMessage());
        }
        return LanguageManager.DEFAULT;
    }

    public static LanguageManager getLanguage(final Account account){
        return getLanguageByISO(account.getLanguageISO());
    }

    public static LanguageManager getPlayerLocaleLanguage(final Player player){
        return LanguageManager.getLanguageByISO(player.getLocale());
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
