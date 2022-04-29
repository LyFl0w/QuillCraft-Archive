package net.quillcraft.parkourpvp.listener.block;

import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.InGameStatus;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class BlockBreakListener implements Listener{

    private final ArrayList<Location> blocksPlaced;
    private final ParkourPvP parkourPvP;
    public BlockBreakListener(ParkourPvP parkourPvP, BlockPlaceListener blockPlaceListener){
        this.parkourPvP = parkourPvP;
        this.blocksPlaced = blockPlaceListener.getBlocksPlaced();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(!parkourPvP.getGameManager().getInGameStatus().actualInGameStatusIs(InGameStatus.PVP)) event.setCancelled(true);

        // BREAK ONLY BLOC PLACED BY PLAYER
        final Location location = event.getBlock().getLocation();
        if(!blocksPlaced.contains(location)){
            event.setCancelled(true);
            return;
        }
        blocksPlaced.remove(location);
    }
}
