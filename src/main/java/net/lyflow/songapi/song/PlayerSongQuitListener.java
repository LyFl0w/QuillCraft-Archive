package net.lyflow.songapi.song;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerSongQuitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void playerQuitEvent(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (RadioPlayer.playerListenRadio(player)) {
            RadioPlayer.getRadioPlayer(player).removePlayer(player);
        }
    }

}
