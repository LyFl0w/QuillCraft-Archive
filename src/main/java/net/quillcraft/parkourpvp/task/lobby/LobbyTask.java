package net.quillcraft.parkourpvp.task.lobby;

import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.player.PlayerDataGame;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.scoreboard.JumpScoreboard;
import net.quillcraft.parkourpvp.game.InGameStatus;

import org.bukkit.*;
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

        if((time <= 5 && time > 0) || time == 10 || time == 15 || time == 30 || time == 60){
            final Server server = parkourPvP.getServer();
            final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();
            final List<UUID> playersUUID = parkourPvPGame.getPlayerUUIDList();

            playersUUID.forEach(playerUUID -> {
                final Player player = server.getPlayer(playerUUID);

                if(time == 10 || time == 15 || time == 30 || time == 60){
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1.0f, 0.1f);
                }else{
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.AMBIENT, 0.5f, 2.0f);
                }
            });

            server.broadcastMessage("§6Le jeu démarre dans §b"+time+"§6's");
        }

        if(time == 0){
            final Server server = parkourPvP.getServer();
            final GameManager gameManager = parkourPvP.getGameManager();
            final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();

            gameManager.setInGameStatus(InGameStatus.WAITING_BEFORE_JUMP);

            parkourPvPGame.setGameStatus(GeneralGameStatus.IN_GAME);
            parkourPvPGame.updateRedis();

            server.broadcastMessage("§1Fin de l'attente : -> démarrage du jeu !");

            try{
                //Start Wait Jump timer
                TaskManager.WAIT_BEFORE_JUMP_TASK_MANAGER.getCustomTaskManager().runTaskTimer(0L, 20L);
            }catch(TaskOverflowException e){
                e.printStackTrace();
            }

            final List<UUID> playersUUID = parkourPvPGame.getPlayerUUIDList();
            final Location startLocation = gameManager.getCheckPoints().get(0).getLocation();
            playersUUID.forEach(playerUUID -> {
                final Player player = server.getPlayer(playerUUID);

                player.teleport(startLocation);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1.0f, 1.0f);
                player.getInventory().setItem(4, new ItemBuilder(Material.SLIME_BALL).setName("§bRespawn").toItemStack());

                gameManager.getPlayersData().put(player.getName(), new PlayerDataGame(playerUUID, gameManager.getDefaultWorldName()));
                gameManager.getCheckPoints().get(0).getPlayers().add(playerUUID);

                //Hide players
                playersUUID.forEach(otherPlayersUUID -> player.hidePlayer(parkourPvP, server.getPlayer(otherPlayersUUID)));

                //Change Scoreboard
                new JumpScoreboard(parkourPvP).setScoreboard(player);
            });

            //Stop Lobby timer
            cancel();
            return;
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
