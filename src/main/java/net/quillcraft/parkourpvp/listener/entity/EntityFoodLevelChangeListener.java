package net.quillcraft.parkourpvp.listener.entity;

import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.InGameStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class EntityFoodLevelChangeListener implements Listener{

    private final ParkourPvP parkourPvP;
    public EntityFoodLevelChangeListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onEntityFoodLevelChange(FoodLevelChangeEvent event){
        if(parkourPvP.getGameManager().getInGameStatus().actualInGameStatusIs(InGameStatus.PVP)) return;

        event.setCancelled(true);
    }
}
