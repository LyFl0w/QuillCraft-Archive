package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.commons.game.GameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.parkourpvp.ParkourPvP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerQuitListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerQuitListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void playerJoinEvent(PlayerQuitEvent event){
        final Player player = event.getPlayer();
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();

        final List<Player> playerList = parkourPvPGame.getPlayerList();
        if(!playerList.contains(player)) return;

        if(parkourPvPGame.actualGameStatusIs(GameStatus.STARTING)){
            if(parkourPvPGame.isFullyFilled()) parkourPvPGame.setGameStatus(GameStatus.PLAYER_WAITING);
        }

        parkourPvPGame.getPlayerList().remove(player);

    }

}
