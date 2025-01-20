package net.quillcraft.parkourpvp.scoreboard;

import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.manager.ScoreboardManager;
import net.quillcraft.core.utils.TimeUtils;
import net.quillcraft.core.utils.builders.scoreboard.ObjectiveBuilder;
import net.quillcraft.core.utils.builders.scoreboard.ScoreboardBuilder;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.task.pvp.PvPTask;
import net.quillcraft.parkourpvp.task.pvp.PvPTaskManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.concurrent.TimeUnit;

public class PvPScoreboard implements ScoreboardManager{

    private final ParkourPvP parkourPvP;
    public PvPScoreboard(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    public void setScoreboard(Player player){
        final ScoreboardBuilder scoreboardBuilder = new ScoreboardBuilder(parkourPvP);

        scoreboardBuilder.addObjective(new ObjectiveBuilder("sbs", "§lParkourPvP", DisplaySlot.SIDEBAR)
                .addScore(14, "§a")
                .addScore(13, "Map : "+parkourPvP.getGameManager().getDefaultWorldName())
                .addScore(12, getPlayerSizeLine())
                .addScore(11, getCoinsLine(player.getName()))
                .addScore(10, "§b")
                .addScore(9, getTimeLine())
                .addScore(8, "§c")
                .addScore(7, getKillLine(player.getName()))
                .addScore(6, "§d")
                .addScore(5, "§6mc.quillcraft.fr"));

        scoreboardBuilder.addPlayer(player);
        scoreboardBuilder.updateScoreboard();

        parkourPvP.getGameManager().getScoreboardBuilderHashMap().put(player.getName(), scoreboardBuilder);
    }

    public void updateCoins(String playerName){
        parkourPvP.getGameManager().getScoreboardBuilderHashMap().get(playerName)
                .updateScore("sbs", 11, getCoinsLine(playerName)).updateScoreboard();
    }

    private String getCoinsLine(String playerName){
        return  "Coins : §e"+parkourPvP.getGameManager().getPlayersDataGame().get(playerName).getCoins();
    }

    public void updatePlayersSize(){
        final String newLine = getPlayerSizeLine();
        parkourPvP.getGameManager().getScoreboardBuilderHashMap().values().stream().parallel()
                .forEach(scoreboardBuilder -> scoreboardBuilder.updateScore("sbs", 12, newLine).updateScoreboard());
    }

    private String getPlayerSizeLine(){
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();
        return "Joueurs : §a"+parkourPvPGame.getPlayerUUIDList().size()+"/"+parkourPvPGame.getGameProperties().getMaxPlayer();
    }

    public void updateKill(String playerName){
        parkourPvP.getGameManager().getScoreboardBuilderHashMap().get(playerName)
                .updateScore("sbs", 7, getKillLine(playerName)).updateScoreboard();
    }

    private String getKillLine(String playerName){
        return  "Kill : §e"+parkourPvP.getGameManager().getPlayersDataGame().get(playerName).getKill();
    }

    public void updateTime(){
        final String newLine = getTimeLine();
        parkourPvP.getGameManager().getScoreboardBuilderHashMap().values().stream().parallel()
                .forEach(scoreboardBuilder -> scoreboardBuilder.updateScore("sbs", 9, newLine).updateScoreboard());
    }

    private String getTimeLine(){
        final PvPTask pvpTask = ((PvPTaskManager)TaskManager.PVP_TASK_MANAGER.getCustomTaskManager()).getTask();
        return "Temps : "+new TimeUtils(pvpTask.getTimeToReach()-pvpTask.getTime(), TimeUnit.SECONDS, "m:ss").formatToTimer();
    }

}
