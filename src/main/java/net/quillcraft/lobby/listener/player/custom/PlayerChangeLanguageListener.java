package net.quillcraft.lobby.listener.player.custom;

import net.quillcraft.core.event.player.PlayerChangeLanguageEvent;
import net.quillcraft.core.utils.Title;
import net.quillcraft.lobby.inventory.InventoryLobby;
import net.quillcraft.lobby.manager.LanguageManager;
import net.quillcraft.lobby.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChangeLanguageListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChange(PlayerChangeLanguageEvent event){
        final Player player = event.getPlayer();
        final LanguageManager languageManager = LanguageManager.getLanguageByISO(event.getNewLanguageISO());

        InventoryLobby.setDefaultInventory(player, languageManager, event.getAccount().getVisibility());
        new Title(player).sendTablistTitle(languageManager.getMessage(Text.TABLIST_LOBBY_HEADER), languageManager.getMessage(Text.TABLIST_LOBBY_FOOTER));
    }

}
