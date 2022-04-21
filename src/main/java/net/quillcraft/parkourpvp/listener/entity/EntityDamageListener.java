package net.quillcraft.parkourpvp.listener.entity;

import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.ParkourPvP;

import net.quillcraft.parkourpvp.status.InGameStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener{

    private final ParkourPvP parkourPvP;
    public EntityDamageListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event){
        if(event.getEntity() instanceof final Player player){
            final GameManager gameManager = parkourPvP.getGameManager();

            if(gameManager.getInGameStatus().actualInGameStatusIs(InGameStatus.PVP)) return;

            if(event.getDamage() > 1.0d)
                player.teleport(gameManager.getCheckPoints().get(gameManager.getPlayersData().get(player.getName()).getCheckPointID()).getLocation());

            event.setCancelled(true);
        }
    }
}
