package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.commons.game.properties.GameProperties;
import net.quillcraft.commons.game.status.GeneralGameStatus;
import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.InGameStatus;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.scoreboard.LobbyScoreboard;
import net.quillcraft.parkourpvp.task.lobby.LobbyTask;
import net.quillcraft.parkourpvp.task.lobby.LobbyTaskManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerJoinListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) throws TaskOverflowException{
        final Player player = event.getPlayer();
        final GameManager gameManager = parkourPvP.getGameManager();
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();

        if(gameManager.getInGameStatus().actualInGameStatusIs(InGameStatus.WAIT_LOBBY)){
            parkourPvP.getServer().getScheduler().runTaskLater(parkourPvP, () -> player.teleport(gameManager.getLobby()), 20L);
            player.setGameMode(GameMode.SURVIVAL);
        }
        player.setFoodLevel(20);
        player.setHealth(20.0D);
        player.getInventory().clear();
        parkourPvP.getServer().getScheduler().runTaskLater(parkourPvP, () -> player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType())), 20L);

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING_FULL) || parkourPvPGame.actualGameStatusIs(GeneralGameStatus.IN_GAME)){
            // TODO : SPECTATOR MODE
            if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.IN_GAME)){
                final List<UUID> uuidList = parkourPvPGame.getPlayerUUIDList();
                player.teleport(parkourPvP.getServer().getPlayer(uuidList.get(new SecureRandom().nextInt(uuidList.size()))));
            }

            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("§cMode specateur activé (beta donc non fonctionnel encore)");

            event.setJoinMessage("");
            return;
        }

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING)){
            final List<UUID> uuidList = parkourPvPGame.getPlayerUUIDList();
            final GameProperties gameProperties = parkourPvPGame.getGameProperties();

            uuidList.add(player.getUniqueId());

            final LobbyScoreboard lobbyScoreboard = new LobbyScoreboard(parkourPvP);
            lobbyScoreboard.updatePlayersSize();
            lobbyScoreboard.setScoreboard(player);

            final LobbyTaskManager lobbyTaskManager = (LobbyTaskManager) TaskManager.LOBBY_TASK_MANAGER.getCustomTaskManager();
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
