package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.core.utils.Title;
import net.quillcraft.parkourpvp.GameData;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.CheckPoint;
import net.quillcraft.parkourpvp.game.PlayerData;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class PlayerMoveListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerMoveListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onPLayerMoveEvent(PlayerMoveEvent event){
        final GameData gameData = parkourPvP.getGameData();

        switch(gameData.getInGameStatus()){
            case WAITING_BEFORE_JUMP, WAITING_BEFORE_PVP -> {
                if(event.getFrom().distanceSquared(event.getTo()) > 0) event.setCancelled(true);
            }
            case JUMP -> {
                final Location from = event.getFrom();
                final Location to = event.getTo();

                //Si le joueur ne bouge pas d'un block au moins, alors on ne fait rien (optimization du nombre de calculs)
                if(!(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ())) return;

                final Player player = event.getPlayer();
                final PlayerData playerData = gameData.getPlayersData().get(player.getName());
                final Optional<CheckPoint> checkPointOptional = gameData.getCheckPoints().stream().parallel()
                        .filter(checkPoint -> checkPoint.getId() > playerData.getCheckPointID() && checkPoint.getLocation().distanceSquared(player.getLocation()) <= 1)
                        .findAny();

                // Si il n'y pas de checkpoint, alors ne rien faire
                if(checkPointOptional.isEmpty()) return;

                final CheckPoint checkPoint = checkPointOptional.get();
                final String nextMessage = checkPoint.addPlayer(playerData);
                final int position = checkPoint.getPlayers().size();

                player.sendMessage("Vous êtes arrivé en "+(position > 1 ? position+"ième" : position+"er")+" au checkpoint n°"+checkPoint.getId());
                new Title(player).sendActionBar(nextMessage);

                playerData.setCheckPointID(checkPoint.getId());

                //TODO : AUTO RESPAWN

            }
        }

    }
}
