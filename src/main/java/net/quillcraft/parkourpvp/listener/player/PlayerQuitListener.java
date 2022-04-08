package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.scoreboard.GameScoreboard;

import net.quillcraft.parkourpvp.scoreboard.LobbyScoreboard;
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

        playerList.remove(player.getUniqueId());

        final String playerName = player.getName();
        parkourPvP.getGameData().getScoreboardBuilderHashMap().remove(playerName);

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING_FULL)){
            if(!parkourPvPGame.isFullyFilled()) parkourPvPGame.setGameStatus(GeneralGameStatus.PLAYER_WAITING);
        }else if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.IN_GAME)){
            parkourPvP.getServer().broadcastMessage("§c"+playerName+" a quitté le jeu !");
            GameScoreboard.updatePlayersSize(parkourPvP);
        }
        parkourPvPGame.updateRedis();

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING)){
            LobbyScoreboard.updatePlayersSize(parkourPvP);
            parkourPvPGame.searchPlayer();
        }
    }

}
