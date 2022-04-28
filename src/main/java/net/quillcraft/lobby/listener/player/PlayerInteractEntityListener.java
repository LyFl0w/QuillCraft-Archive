package net.quillcraft.lobby.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener{

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        event.setCancelled(true);
    }

}
