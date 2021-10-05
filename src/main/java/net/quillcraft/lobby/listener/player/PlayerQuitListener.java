package net.quillcraft.lobby.listener.player;

import net.quillcraft.lobby.manager.LanguageManager;
import net.quillcraft.lobby.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        final Player player = event.getPlayer();

        Bukkit.getOnlinePlayers().stream().parallel().forEach(players ->
                players.sendMessage(LanguageManager.getLanguage(players).getMessage(Text.PLAYER_LEAVE).replace("%PLAYER%", player.getDisplayName())));

        event.setQuitMessage("");
    }

}
