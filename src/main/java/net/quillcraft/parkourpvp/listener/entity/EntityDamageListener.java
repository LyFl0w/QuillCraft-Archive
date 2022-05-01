package net.quillcraft.parkourpvp.listener.entity;

import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.ParkourPvP;

import net.quillcraft.parkourpvp.game.InGameStatus;
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
            final InGameStatus inGameStatus = gameManager.getInGameStatus();

            if(inGameStatus.actualInGameStatusIs(InGameStatus.PVP)) return;

            if(inGameStatus.actualInGameStatusIs(InGameStatus.WAIT_LOBBY)){
                event.setCancelled(true);
                return;
            }

            if(inGameStatus.actualInGameStatusIs(InGameStatus.JUMP) && event.getDamage() > 2.0d){
                player.teleport(gameManager.getCheckPoints().get(gameManager.getPlayersDataGame().get(player.getName()).getCheckPointID()).getLocation());
            }

            event.setCancelled(true);
        }
    }
}
