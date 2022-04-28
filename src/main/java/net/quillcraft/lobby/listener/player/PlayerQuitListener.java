package net.quillcraft.lobby.listener.player;

import net.quillcraft.core.manager.LanguageManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.lumy.api.text.Text;

public class PlayerQuitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event){
        final Player player = event.getPlayer();

        Bukkit.getOnlinePlayers().stream().parallel().forEach(players ->
                players.sendMessage(LanguageManager.getLanguage(players).getMessage(Text.LOBBY_PLAYER_LEAVE).replace("%PLAYER%", player.getDisplayName())));

        PlayerJoinListener.scoreboardBuilder.removePlayer(player);

        event.setQuitMessage("");
    }

}
