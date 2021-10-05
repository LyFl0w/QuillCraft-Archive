package net.quillcraft.lobby.listener.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class ExplosionPrimeListener implements Listener {

    @EventHandler
    public void onEntityExplode(ExplosionPrimeEvent event){
        event.setRadius(0f);
        event.setFire(false);
    }

}
