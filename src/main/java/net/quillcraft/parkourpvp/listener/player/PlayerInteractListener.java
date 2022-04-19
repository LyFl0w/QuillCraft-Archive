package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.parkourpvp.ParkourPvP;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerInteractListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onPLayerInteractEvent(PlayerInteractEvent event){
        if(event.getHand() == EquipmentSlot.OFF_HAND) return;

        switch(parkourPvP.getGameData().getInGameStatus()){
            //TODO : Interaction with respawn item
            case JUMP -> {}
            //TODO : Interaction with item to get shop inventory
            case WAITING_BEFORE_PVP -> {}
        }

    }
}
