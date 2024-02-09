package net.quillcraft.lobby.listener.player.custom;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.data.redis.RedisManager;
import net.quillcraft.core.event.player.PlayerChangeLanguageEvent;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.Title;
import net.quillcraft.core.utils.builders.ScoreboardBuilder;
import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.inventory.InventoryLobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.quillcraft.lumy.api.text.TextList;
import org.redisson.api.RedissonClient;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

public class PlayerChangeLanguageListener implements Listener {

    private final QuillCraftLobby quillCraftLobby;
    private final RedissonClient redissonClient;

    public PlayerChangeLanguageListener(QuillCraftLobby quillCraftLobby) {
        this.quillCraftLobby = quillCraftLobby;
        this.redissonClient = RedisManager.WEB_API.getRedisAccess().getRedissonClient();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangeLanguage(PlayerChangeLanguageEvent event) {
        final Player player = event.getPlayer();
        final LanguageManager languageManager = LanguageManager.getLanguageByISO(event.getNewLanguageISO());

        try {
            final Account account = new AccountProvider(player).getAccount();
            // Display Scoreboard
            final String[] splitLang = languageManager.getISO().split("_");
            final ScoreboardBuilder scoreboardBuilder = new ScoreboardBuilder("QuillCraft").setLines(
                    DateFormat.getDateInstance(DateFormat.SHORT, new Locale(splitLang[0], splitLang[1])).format(new Date()),
                    "Lobby: 1", "Connecté: "+redissonClient.getAtomicLong("players.size").get(),
                    "",
                    "⟫ "+player.getName(),
                    "| Rank: "+account.getRankID(),
                    "| QuillCoins: "+account.getQuillCoins(),
                    "",
                    "https://quillcraft.fr"
            );

            quillCraftLobby.getScoreboardManager().addScoreboardBuilder(player, scoreboardBuilder);
        } catch(AccountNotFoundException exception) {
            quillCraftLobby.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }

        InventoryLobby.setDefaultInventory(player, languageManager, event.getAccount().getVisibility());
        new Title(player).sendTablistTitle(languageManager.getMessage(TextList.TABLIST_DEFAULT));
    }

}
