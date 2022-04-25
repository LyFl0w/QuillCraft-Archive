package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.parkourpvp.ParkourPvP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerDropItemListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        switch(parkourPvP.getGameManager().getInGameStatus()){
            case PVP, WAITING_BEFORE_PVP -> {}
            default -> event.setCancelled(true);
        }
    }

}
