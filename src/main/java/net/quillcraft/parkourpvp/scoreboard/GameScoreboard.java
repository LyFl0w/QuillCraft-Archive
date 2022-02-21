package net.quillcraft.parkourpvp.scoreboard;

import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.utils.builders.scoreboard.ObjectiveBuilder;
import net.quillcraft.core.utils.builders.scoreboard.ScoreboardBuilder;
import net.quillcraft.parkourpvp.ParkourPvP;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class GameScoreboard{

    private final ParkourPvP parkourPvP;
    public GameScoreboard(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    //TODO : SPECTATOR SCOREBOARD IN GAME (SAME AS GameScoreboard but without the lines 11 and 12)

    public void setGameScoreboard(Player player){
        final ScoreboardBuilder scoreboardBuilder = new ScoreboardBuilder(parkourPvP);

        scoreboardBuilder.addObjective(new ObjectiveBuilder("sbs", "§lParkourPvP", DisplaySlot.SIDEBAR)
                .addScore(5, getPlayerSizeLine())
                .addScore(6, "§c")
                .addScore(7, "Temps : 00:00")
                .addScore(8, "1er : ?")
                .addScore(9, "2ème : ?")
                .addScore(10, "3ème : ?")
                .addScore(11, "§b")
                .addScore(12, "Coins : §c0"));

        scoreboardBuilder.addPlayer(player);
        scoreboardBuilder.updateScoreboard();

        parkourPvP.getScoreboardBuilderHashMap().put(player.getName(), scoreboardBuilder);
    }

    public void updatePlayersSize(){
        final String newLine = getPlayerSizeLine();
        parkourPvP.getScoreboardBuilderHashMap().values().stream().parallel()
                .forEach(scoreboardBuilder -> scoreboardBuilder.updateScore("sbs", 5, newLine).updateScoreboard());
    }

    private String getPlayerSizeLine(){
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();
        return "Joueurs : §a"+parkourPvPGame.getPlayerUUIDList().size()+"/"+parkourPvPGame.getGameProperties().getMaxPlayer();
    }

}
