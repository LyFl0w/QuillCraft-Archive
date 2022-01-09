package net.quillcraft.bungee.manager;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
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
        return Arrays.stream(values()).parallel().filter(textManager -> textManager.getISO().equalsIgnoreCase(isoLanguage)).findFirst().orElse(LanguageManager.ENGLISH_US);
    }

    public static LanguageManager getLanguage(final ProxiedPlayer player){
        try{
            return getLanguage(new AccountProvider(player).getAccount());
        }catch(AccountNotFoundException e){
            e.printStackTrace();
        }
        return LanguageManager.DEFAULT;
    }

    public static LanguageManager getLanguage(final Account account){
        return getLanguageByISO(account.getLanguageISO());
    }

    public static String getMessageByDefaultLanguage(final Text text){
        return DEFAULT.getMessage(text);
    }

    public static List<String> getListMessageByDefaultLanguage(final TextList textList){
        return DEFAULT.getMessage(textList);
    }

    public static TextComponent getMessageComponentByDefaultLanguage(final Text text){
        return new TextComponent(getMessageByDefaultLanguage(text));
    }

    public String getMessage(final Text text){
        final RBucket<String> message = redissonClient.getBucket(getISO()+":"+text.getPath());
        return message.get();
    }

    public TextComponent getMessageComponent(final Text text){
        return new TextComponent(getMessage(text));
    }

    public TextComponent getMessageComponentReplace(final Text text, final String replace, final String to){
        return new TextComponent(getMessage(text).replace(replace, to));
    }

    public List<String> getMessage(final TextList text){
        final RList<String> message = redissonClient.getList(getISO()+":"+text.getPath());
        return message.readAll();
    }

    public String getISO(){
        return iso;
    }

}
