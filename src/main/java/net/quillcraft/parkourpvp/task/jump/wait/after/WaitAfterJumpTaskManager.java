package net.quillcraft.parkourpvp.task.jump.wait.after;

import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.player.PlayerDataGame;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.game.InGameStatus;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WaitAfterJumpTaskManager extends CustomTaskManager{

    public WaitAfterJumpTaskManager(ParkourPvP parkourPvP){
        super(parkourPvP, WaitAfterJumpTask.class);
    }

    @Override
    public WaitAfterJumpTask getTask(){
        return (WaitAfterJumpTask) super.getTask();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ParkourPvP getJavaPlugin(){
        return (ParkourPvP) javaPlugin;
    }

    public void startDefaultTask(){
        final ParkourPvP parkourPvP = getJavaPlugin();
        final Server server = parkourPvP.getServer();
        final GameManager gameManager = parkourPvP.getGameManager();
        final Supplier<Stream<PlayerDataGame>> playersData = () -> gameManager.getPlayersData().values().stream();

        gameManager.setInGameStatus(InGameStatus.WAITING_AFTER_JUMP);

        playersData.get().filter(playerData -> !playerData.hasFinishParkour()).forEach(playerData -> {
            final Player player = server.getPlayer(playerData.getUuid());
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();

            playersData.get().forEach(otherPlayerData -> player.showPlayer(parkourPvP, server.getPlayer(otherPlayerData.getUuid())));
        });

        final Supplier<Stream<PlayerDataGame>> playersFinishParkour = () -> playersData.get().filter(PlayerDataGame::hasFinishParkour);

        if(playersFinishParkour.get().findAny().isPresent()){
            final StringBuilder whoFinishParkour = new StringBuilder("\nVoici la liste des joueurs qui ont finit le parcours :");
            playersFinishParkour.get().sorted(Comparator.comparing(PlayerDataGame::getTimeToFinishParkour)).forEach(playerData -> whoFinishParkour.append("\n • ").append(server.getPlayer(playerData.getUuid()).getName()).append(playerData.getFormatedTimeToFinishParkour(new StringBuilder(" → ")).toString()));
            server.getScheduler().runTaskLater(parkourPvP, () -> {
                server.broadcastMessage(whoFinishParkour.append("\n").toString());
                try{
                    runTaskTimer(0L, 20L);
                }catch(TaskOverflowException e){
                    e.printStackTrace();
                }
            }, 100L);
            return;
        }

        try{
            runTaskTimer(0L, 20L);
        }catch(TaskOverflowException e){
            throw new RuntimeException(e);
        }

    }
}