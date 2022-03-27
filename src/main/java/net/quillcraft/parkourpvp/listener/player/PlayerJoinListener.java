package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.commons.game.GameProperties;
import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.core.utils.builders.scoreboard.ScoreboardBuilder;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.manager.TaskManager;

import net.quillcraft.parkourpvp.scoreboard.LobbyScoreboard;
import net.quillcraft.parkourpvp.task.wait.LobbyTaskManager;
import net.quillcraft.parkourpvp.task.wait.LobbyTask;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerJoinListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) throws TaskOverflowException{
        final Player player = event.getPlayer();
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING)){
            final List<UUID> uuidList = parkourPvPGame.getPlayerUUIDList();
            final GameProperties gameProperties = parkourPvPGame.getGameProperties();

            // GAME IS ALREADY FULL
            // TODO : SPECTATOR MODE
            if(uuidList.size() == gameProperties.getMaxPlayer()){
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage("§cMode specateur activé (beta donc non fonctionnel encore)");
                return;
            }

            uuidList.add(player.getUniqueId());

            new LobbyScoreboard(parkourPvP).setScoreboard(player);

            final LobbyTaskManager lobbyTaskManager = (LobbyTaskManager) TaskManager.STARTING_TASK_MANAGER.getCustomTaskManager();
            final LobbyTask lobbyTask = lobbyTaskManager.getTask();
            if(uuidList.size() == gameProperties.getMaxPlayer()){
                // CAN START AUTO START (15 seconds)
                parkourPvPGame.setGameStatus(GeneralGameStatus.PLAYER_WAITING_FULL);
                if(lobbyTask.getTime() > 15) lobbyTask.setTime(15);
            }else if(uuidList.size() >= parkourPvPGame.getGameProperties().getMinPlayer()){
                // CAN START AUTO START (3 minutes)
                if(!lobbyTaskManager.isRunning()){
                    lobbyTaskManager.runTaskTimer(0L, 20L);
                }
            }else if(lobbyTaskManager.isRunning()){
                lobbyTaskManager.cancel();
            }

            parkourPvPGame.updateRedis();
        }
    }

}
