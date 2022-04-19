package net.quillcraft.parkourpvp.task.lobby;

import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.GameData;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.PlayerData;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.scoreboard.JumpScoreboard;
import net.quillcraft.parkourpvp.status.InGameStatus;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class LobbyTask extends CustomTask{

    // FIXME: 20/02/2022 REMOVE LOBBY FOR WAITING SALLE ( LOBBY IS JUST FOR THE BETA)

    private final ParkourPvP parkourPvP;
    private int time;

    public LobbyTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.time = 60;
        this.parkourPvP = ((LobbyTaskManager)customTaskManager).getJavaPlugin();
    }

    @Override
    public void run(){

        if(time == 0){
            final Server server = parkourPvP.getServer();
            final GameData gameData = parkourPvP.getGameData();
            final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();

            gameData.setInGameStatus(InGameStatus.WAITING_BEFORE_JUMP);

            parkourPvPGame.setGameStatus(GeneralGameStatus.IN_GAME);
            parkourPvPGame.updateRedis();

            server.broadcastMessage("§1Fin de l'attente : -> démarrage du jeu !");

            try{
                //Start Wait Jump timer
                TaskManager.WAIT_JUMP_TASK_MANAGER.getCustomTaskManager().runTaskTimer(0L, 20L);
            }catch(TaskOverflowException e){
                e.printStackTrace();
            }

            final List<UUID> playersUUID = parkourPvPGame.getPlayerUUIDList();
            final Location startLocation = gameData.getCheckPoints().get(0).getLocation();
            playersUUID.forEach(playerUUID -> {
                final Player player = server.getPlayer(playerUUID);

                //Teleport players
                player.teleport(startLocation);
                gameData.getPlayersData().put(player.getName(), new PlayerData(playerUUID));
                gameData.getCheckPoints().get(0).getPlayers().add(playerUUID);

                //Hide players
                playersUUID.forEach(otherPlayersUUID -> player.hidePlayer(parkourPvP, server.getPlayer(otherPlayersUUID)));

                //Change Scoreboard
                new JumpScoreboard(parkourPvP).setScoreboard(player);
            });

            //Stop Lobby timer
            cancel();
            return;
        }

        if((time <= 5 && time > 0) || time == 10 || time == 15 || time == 30 || time == 60){
            parkourPvP.getServer().broadcastMessage("§6Le jeu démarre dans §b"+time+"§6's");
        }

        time--;
    }

    public int getTime(){
        return time;
    }

    public void setTime(int time){
        this.time = time;
    }

}
