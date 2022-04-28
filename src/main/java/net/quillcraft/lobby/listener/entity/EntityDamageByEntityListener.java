package net.quillcraft.lobby.listener.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener{

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        event.setCancelled(true);
    }

}
