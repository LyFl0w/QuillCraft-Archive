package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.InGameStatus;
import net.quillcraft.commons.game.statistiques.parkourpvp.PlayerParkourPvPData;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.scoreboard.PvPScoreboard;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class PlayerDeathListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerDeathListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        final Player player = event.getEntity();
        final GameManager gameManager = parkourPvP.getGameManager();
        final HashMap<String, PlayerParkourPvPData> playersDataGame = gameManager.getPlayersDataGame();

        if(!gameManager.getInGameStatus().actualInGameStatusIs(InGameStatus.PVP)) return;

        playersDataGame.get(player.getName()).setDead();
        player.setGameMode(GameMode.SPECTATOR);

        player.teleport(new Location(gameManager.getWorlds()[1], -6.5, -50, 9.5));

        if(player.getKiller() != null){
            final Player killer = player.getKiller();
            final String killerName = killer.getName();
            final PlayerParkourPvPData PlayerParkourPvPData = playersDataGame.get(killerName);

            PlayerParkourPvPData.addKill();
            new PvPScoreboard(parkourPvP).updateKill(killerName);
        }

        final Supplier<Stream<? extends Player>> survivePlayers = () -> parkourPvP.getServer().getOnlinePlayers().stream().filter(players -> players.getGameMode() == GameMode.SURVIVAL);
        if(survivePlayers.get().count() == 1){
            final Player winner = survivePlayers.get().findFirst().get();
            playersDataGame.get(winner.getName()).setWin();

            winner.setGameMode(GameMode.CREATIVE);
            parkourPvP.getServer().broadcastMessage(winner.getName()+" a gagné la partie !");
            playEndOfTheGame(gameManager);
        }else if(survivePlayers.get().findAny().isEmpty()){
            parkourPvP.getServer().broadcastMessage("§cTout le monde est mort !\nPersonne n'a gagné ");
            playEndOfTheGame(gameManager);
        }

        event.setDeathMessage("");
    }

    private void playEndOfTheGame(GameManager gameManager){
        TaskManager.PVP_TASK_MANAGER.getCustomTaskManager().cancel();

        gameManager.setInGameStatus(InGameStatus.END);

        try{
            TaskManager.END_TASK_MANAGER.getCustomTaskManager().runTaskTimer(0L, 20L);
        }catch(TaskOverflowException e){
            e.printStackTrace();
        }
    }
}
