package net.quillcraft.parkourpvp.scoreboard;

import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.utils.builders.scoreboard.ObjectiveBuilder;
import net.quillcraft.core.utils.builders.scoreboard.ScoreboardBuilder;
import net.quillcraft.parkourpvp.ParkourPvP;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class LobbyScoreboard implements ScoreboardManager{

    // FIXME: 20/02/2022 REMOVE LOBBY FOR WAITING SALLE ( LOBBY IS JUST FOR THE BETA)

    private final ParkourPvP parkourPvP;
    public LobbyScoreboard(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    //TODO : SPECTATOR SCOREBOARD IN GAME (SAME AS GameScoreboard but without the lines 11 and 12)

    public void setScoreboard(Player player){
        final ScoreboardBuilder scoreboardBuilder = new ScoreboardBuilder(parkourPvP);

        scoreboardBuilder.addObjective(new ObjectiveBuilder("sbs", "§lParkourPvP", DisplaySlot.SIDEBAR)
                .addScore(4, getPlayerSizeLine())
                .addScore(5, "§c")
                .addScore(6, "§b§lStats")
                .addScore(7, "Total Jumps: ?")
                .addScore(8, "Parkour Fini: ?")
                .addScore(9, "Temps Parkour: ?")
                .addScore(10, "§b")
                .addScore(11, "Total Kills: ?")
                .addScore(12, "Victoires: ?")
                .addScore(13, "§e")
                .addScore(14, "§bmc.quillcraft.fr"));

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
