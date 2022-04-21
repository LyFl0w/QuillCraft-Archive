package net.quillcraft.parkourpvp.listener.block;

import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.status.InGameStatus;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

public class BlockPlaceListener implements Listener{

    private final ArrayList<Location> blocksPlaced = new ArrayList<>();

    private final int getMaxY;
    private final ParkourPvP parkourPvP;
    public BlockPlaceListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
        this.getMaxY = this.parkourPvP.getGameManager().getFileConfiguration().getInt("pvp.get-max-y");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(!parkourPvP.getGameManager().getInGameStatus().actualInGameStatusIs(InGameStatus.PVP)){
            event.setCancelled(true);
            return;
        }

        final Location location = event.getBlockPlaced().getLocation();
        if(getMaxY < 0 || location.getBlockY() > getMaxY){
            event.getPlayer().sendMessage("§cVous ne pouvez pas poser de blocs au dessus de cette hauteur");
            event.setCancelled(true);
        }

        blocksPlaced.add(location);
    }

    protected ArrayList<Location> getBlocksPlaced(){
        return blocksPlaced;
    }
}
