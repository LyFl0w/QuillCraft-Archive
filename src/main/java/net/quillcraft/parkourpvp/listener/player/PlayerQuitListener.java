package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.parkourpvp.ParkourPvP;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public class PlayerQuitListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerQuitListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }
    
    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event){
        final Player player = event.getPlayer();
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();

        final List<UUID> playerList = parkourPvPGame.getPlayerUUIDList();
        if(!playerList.contains(player.getUniqueId())) return;

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING_FULL)){
            if(parkourPvPGame.isFullyFilled()) parkourPvPGame.setGameStatus(GeneralGameStatus.PLAYER_WAITING);
        }

        playerList.remove(player.getUniqueId());
        parkourPvPGame.updateRedis();
        parkourPvPGame.searchPlayer();
    }

}
