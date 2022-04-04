package net.quillcraft.parkourpvp.scoreboard;

import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.manager.ScoreboardManager;
import net.quillcraft.core.utils.builders.scoreboard.ObjectiveBuilder;
import net.quillcraft.core.utils.builders.scoreboard.ScoreboardBuilder;
import net.quillcraft.parkourpvp.ParkourPvP;

import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.task.game.GameTask;
import net.quillcraft.parkourpvp.task.game.GameTaskManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GameScoreboard implements ScoreboardManager{

    private final ParkourPvP parkourPvP;
    public GameScoreboard(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    //TODO : SPECTATOR SCOREBOARD IN GAME (SAME AS GameScoreboard but without the lines 11 and 12)

    public void setScoreboard(Player player){
        final ScoreboardBuilder scoreboardBuilder = new ScoreboardBuilder(parkourPvP);

        scoreboardBuilder.addObjective(new ObjectiveBuilder("sbs", "§lParkourPvP", DisplaySlot.SIDEBAR)
                .addScore(14, "§a")
                .addScore(13, getPlayerSizeLine())
                .addScore(12, "Coins : §e0")
                .addScore(11, "§b")
                .addScore(10, getTimeLine())
                .addScore(9, "1er : ?")
                .addScore(8, "2ème : ?")
                .addScore(7, "3ème : ?")
                .addScore(6, "§l")
                .addScore(5, "§6mc.quillcraft.fr"));

        scoreboardBuilder.addPlayer(player);
        scoreboardBuilder.updateScoreboard();

        parkourPvP.getGameData().getScoreboardBuilderHashMap().put(player.getName(), scoreboardBuilder);
    }

    public void updatePlayersSize(){
        final String newLine = getPlayerSizeLine();
        parkourPvP.getGameData().getScoreboardBuilderHashMap().values().stream().parallel()
                .forEach(scoreboardBuilder -> scoreboardBuilder.updateScore("sbs", 13, newLine).updateScoreboard());
    }

    private String getPlayerSizeLine(){
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();
        return "Joueurs : §a"+parkourPvPGame.getPlayerUUIDList().size()+"/"+parkourPvPGame.getGameProperties().getMaxPlayer();
    }

    public void updateTime(){
        final String newLine = getTimeLine();
        parkourPvP.getGameData().getScoreboardBuilderHashMap().values().stream().parallel()
                .forEach(scoreboardBuilder -> scoreboardBuilder.updateScore("sbs", 10, newLine).updateScoreboard());
    }

    private String getTimeLine(){
        final GameTask gameTask = ((GameTaskManager)TaskManager.GAME_TASK_MANAGER.getCustomTaskManager()).getTask();
        return "Temps : "+new SimpleDateFormat("mm:ss")
                .format(new Date(gameTask.getTimeToWait()-gameTask.getTime()*1000L));
     }

}
