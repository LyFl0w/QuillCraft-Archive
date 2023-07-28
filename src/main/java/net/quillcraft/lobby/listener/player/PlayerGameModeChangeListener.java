package net.quillcraft.lobby.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class PlayerGameModeChangeListener implements Listener {

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        //event.setCancelled(true);
    }

}
