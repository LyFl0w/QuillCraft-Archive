package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.commons.game.status.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.InGameStatus;
import net.quillcraft.commons.game.statistiques.parkourpvp.PlayerParkourPvPData;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.scoreboard.JumpScoreboard;
import net.quillcraft.parkourpvp.scoreboard.LobbyScoreboard;
import net.quillcraft.parkourpvp.scoreboard.PvPScoreboard;
import net.quillcraft.parkourpvp.task.jump.wait.after.WaitAfterJumpTaskManager;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerQuitListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerQuitListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }
    
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        final Player player = event.getPlayer();
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();

        final List<UUID> playerList = parkourPvPGame.getPlayerUUIDList();
        if(!playerList.contains(player.getUniqueId())) return;

        playerList.remove(player.getUniqueId());

        final GameManager gameManager = parkourPvP.getGameManager();
        final String playerName = player.getName();

        gameManager.getScoreboardBuilderHashMap().remove(playerName);

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING_FULL)){
            if(!parkourPvPGame.isFullyFilled()) parkourPvPGame.setGameStatus(GeneralGameStatus.PLAYER_WAITING);
        }else if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.IN_GAME)){
            parkourPvP.getServer().broadcastMessage("§c"+playerName+" a quitté le jeu !");

            switch(gameManager.getInGameStatus()){
                case WAITING_BEFORE_JUMP, JUMP -> {
                    final HashMap<String, PlayerParkourPvPData> playersDataGame = gameManager.getPlayersDataGame();
                    playersDataGame.remove(playerName);

                    new JumpScoreboard(parkourPvP).updatePlayersSize();

                    if(playersDataGame.size() == 0){
                        try{
                            TaskManager.END_TASK_MANAGER.getCustomTaskManager().runTaskTimer(0L, 20L);
                        }catch(TaskOverflowException e){
                            e.printStackTrace();
                        }
                        return;
                    }

                    final Server server = parkourPvP.getServer();

                    // EVERYONE HAS FINISHED PARKOUR
                    if(gameManager.getCheckPoints().get(gameManager.getCheckPoints().size()).getPlayers().size() >= parkourPvPGame.getPlayerUUIDList().size()){
                        // STOP JUMP TASK
                        TaskManager.JUMP_TASK_MANAGER.getCustomTaskManager().cancel();

                        // START JUMP END
                        server.broadcastMessage("\n§bTout le monde a finit le parkour dans les temps impartis");

                        ((WaitAfterJumpTaskManager)TaskManager.WAIT_AFTER_JUMP_TASK_MANAGER.getCustomTaskManager()).startDefaultTask();
                    }
                }

                case WAITING_AFTER_JUMP -> new JumpScoreboard(parkourPvP).updatePlayersSize();

                case PVP, WAITING_BEFORE_PVP -> {
                    new PvPScoreboard(parkourPvP).updatePlayersSize();

                    final List<UUID> playerUUIDList = parkourPvPGame.getPlayerUUIDList();

                    if(playerUUIDList.size() <= 1){
                        if(playerUUIDList.size() == 1){
                            final Player winner = parkourPvP.getServer().getPlayer(playerUUIDList.get(0));
                            parkourPvP.getGameManager().getPlayersDataGame().get(winner.getName()).setWin();

                            winner.setGameMode(GameMode.CREATIVE);
                            parkourPvP.getServer().broadcastMessage(winner.getName()+" a gagné la partie !");
                        }else{
                            parkourPvP.getServer().broadcastMessage("§cTout le monde est mort !\nPersonne n'a gagné ");
                        }

                        if(gameManager.getInGameStatus().actualInGameStatusIs(InGameStatus.PVP)){
                            TaskManager.PVP_TASK_MANAGER.getCustomTaskManager().cancel();
                        }else{
                            TaskManager.WAIT_BEFORE_PVP_TASK_MANAGER.getCustomTaskManager().cancel();
                        }

                        try{
                            TaskManager.END_TASK_MANAGER.getCustomTaskManager().runTaskTimer(0L, 20L);
                        }catch(TaskOverflowException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        parkourPvPGame.updateRedis();

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING)){
            new LobbyScoreboard(parkourPvP).updatePlayersSize();
            parkourPvPGame.searchPlayer();
        }

        event.setQuitMessage("");
    }

}
