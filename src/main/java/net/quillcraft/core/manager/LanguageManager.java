package net.quillcraft.core.manager;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.redis.RedisManager;
import org.bukkit.entity.Player;
import org.lumy.api.text.Text;
import org.lumy.api.text.TextList;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public enum LanguageManager {

    ENGLISH_US("en_us"), FRENCH("fr_fr"),

    DEFAULT(ENGLISH_US.getISO());

    private final static RedissonClient redissonClient = RedisManager.TEXT.getRedisAccess().getRedissonClient();
    private final String iso;

    LanguageManager(final String isoLanguage) {
        this.iso = isoLanguage;
    }

    public static LanguageManager getLanguageByISO(final String isoLanguage) {
        return Arrays.stream(values()).parallel().filter(textManager -> textManager.getISO().equalsIgnoreCase(isoLanguage)).findFirst().orElse(LanguageManager.DEFAULT);
    }

    public static LanguageManager getLanguage(final Player player) {
        try {
            return getLanguage(new AccountProvider(player).getAccount());
        } catch(AccountNotFoundException exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return LanguageManager.DEFAULT;
    }

    public static LanguageManager getLanguage(final Account account) {
        return getLanguageByISO(account.getLanguageISO());
    }

    public static LanguageManager getPlayerLocaleLanguage(final Player player) {
        return LanguageManager.getLanguageByISO(player.getLocale());
    }

    public String getMessage(final Text text) {
        final RBucket<String> message = redissonClient.getBucket(getISO()+":"+text.getPath());
        return message.get();
    }

    public List<String> getMessage(final TextList text) {
        final RList<String> message = redissonClient.getList(getISO()+":"+text.getPath());
        return message.readAll();
    }

    public String getISO() {
        return iso;
    }

}
