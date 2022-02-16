package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.parkourpvp.ParkourPvP;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerJoinListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event){
        final Player player = event.getPlayer();
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING)){
            // FIXME: 28/01/2022  NOT KICK PLAYER BUT RETOUR TO THE LOBBY
            if(parkourPvPGame.isFullyFilled()){
                player.kickPlayer("Server is full");
                return;
            }
            parkourPvPGame.getPlayerUUIDList().add(player.getUniqueId());
            parkourPvPGame.updateRedis();
        }
    }

}
