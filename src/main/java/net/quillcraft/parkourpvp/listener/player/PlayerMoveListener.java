package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.commons.game.statistiques.parkourpvp.PlayerParkourPvPData;
import net.quillcraft.core.utils.Title;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.checkpoint.CheckPoint;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.scoreboard.JumpScoreboard;
import net.quillcraft.parkourpvp.task.jump.JumpTask;
import net.quillcraft.parkourpvp.task.jump.wait.after.WaitAfterJumpTaskManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerMoveListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerMoveListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onPLayerMoveEvent(PlayerMoveEvent event){
        if(event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

        final GameManager gameManager = parkourPvP.getGameManager();

        switch(gameManager.getInGameStatus()){
            case WAITING_BEFORE_JUMP, WAITING_BEFORE_PVP -> {
                if(event.getFrom().distanceSquared(event.getTo()) > 0) event.setCancelled(true);
            }
            case JUMP -> {
                final Location from = event.getFrom();
                final Location to = event.getTo();

                if(from.getBlockY() > to.getBlockY()){
                    final Player player = event.getPlayer();
                    final PlayerParkourPvPData PlayerParkourPvPData = gameManager.getPlayersDataGame().get(player.getName());

                    // AUTO RESPAWN
                    final Location currentCheckPointLocation = gameManager.getCheckPoints().get(PlayerParkourPvPData.getCheckPointID()).getLocation();
                    if((gameManager.getCheckPoints().get(PlayerParkourPvPData.getCheckPointID()+1).getLocation().getBlockY()-player.getLocation().getBlockY()) >= 10
                            && (currentCheckPointLocation.getBlockY()-player.getLocation().getBlockY()) >= 10 && player.getFallDistance() >= 20.0F){
                        PlayerParkourPvPData.addRespawn();
                        player.setFallDistance(0.0F);
                        player.teleport(currentCheckPointLocation);
                        return;
                    }
                }

                //Si le joueur ne bouge pas d'un block au moins, alors on ne fait rien (optimization du nombre de calculs)
                if(!(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ() || from.getBlockY() > to.getBlockY())) return;

                final Player player = event.getPlayer();
                final PlayerParkourPvPData PlayerParkourPvPData = gameManager.getPlayersDataGame().get(player.getName());
                final Optional<CheckPoint> checkPointOptional = gameManager.getCheckPoints().stream().parallel()
                        .filter(checkPoint -> checkPoint.getId() > PlayerParkourPvPData.getCheckPointID() && checkPoint.getLocation().distanceSquared(player.getLocation()) <= 2)
                        .findAny();

                // Si il n'y pas de checkpoint, alors ne rien faire
                if(checkPointOptional.isEmpty()) return;

                final CheckPoint checkPoint = checkPointOptional.get();
                final String nextMessage = checkPoint.addPlayer(PlayerParkourPvPData);
                final int position = checkPoint.getPlayers().size();

                PlayerParkourPvPData.setCheckPointID(checkPoint.getId());

                player.sendMessage("Vous êtes arrivé en "+(position > 1 ? position+"ième" : position+"er")+" au checkpoint n°"+checkPoint.getId());
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.AMBIENT, 10.0f, 1.0f);

                new Title(player).sendActionBar(nextMessage);
                new JumpScoreboard(parkourPvP).updateCoins(player.getName());

                // FIN DU PARKOUR
                if(checkPoint.getId()+1 == gameManager.getCheckPoints().size()){
                    final Server server = parkourPvP.getServer();
                    final List<UUID> playersUUID = parkourPvP.getParkourPvPGame().getPlayerUUIDList();

                    PlayerParkourPvPData.setTimeToFinishParkour(System.currentTimeMillis()-((JumpTask)TaskManager.JUMP_TASK_MANAGER.getCustomTaskManager().getTask()).getStartedAtTimeMillis());

                    player.setGameMode(GameMode.SPECTATOR);
                    player.getInventory().clear();

                    playersUUID.forEach(otherPlayersUUID -> player.showPlayer(parkourPvP, server.getPlayer(otherPlayersUUID)));

                    // EVERYONE HAS FINISHED PARKOUR
                    if(checkPoint.getPlayers().size() >= playersUUID.size()){
                        // STOP JUMP TASK
                        TaskManager.JUMP_TASK_MANAGER.getCustomTaskManager().cancel();

                        // START JUMP END
                        server.broadcastMessage("\n§bTout le monde a finit le parkour dans les temps impartis");

                        ((WaitAfterJumpTaskManager)TaskManager.WAIT_AFTER_JUMP_TASK_MANAGER.getCustomTaskManager()).startDefaultTask();
                    }
                }
            }
        }

    }
}
