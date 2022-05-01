package net.quillcraft.lobby.listener.player.custom;

import net.quillcraft.core.event.player.PlayerChangeLanguageEvent;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.Title;
import net.quillcraft.lobby.inventory.InventoryLobby;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.lumy.api.text.TextList;

public class PlayerChangeLanguageListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangeLanguage(PlayerChangeLanguageEvent event){
        final Player player = event.getPlayer();
        final LanguageManager languageManager = LanguageManager.getLanguageByISO(event.getNewLanguageISO());

        InventoryLobby.setDefaultInventory(player, languageManager, event.getAccount().getVisibility());
        new Title(player).sendTablistTitle(languageManager.getMessage(TextList.TABLIST_DEFAULT));
    }

}
