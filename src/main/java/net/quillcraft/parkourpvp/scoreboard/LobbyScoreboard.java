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
                .addScore(14, "§c")
                .addScore(13, "§b§lStats")
                .addScore(12, "Total Jumps: ?")
                .addScore(11, "Parkour Fini: ?")
                .addScore(10, "Temps Parkour: ?")
                .addScore(9, "§b")
                .addScore(8, "Total Kills: ?")
                .addScore(7, "Victoires: ?")
                .addScore(6, "§e")
                .addScore(5, getPlayerSizeLine())
                .addScore(4, "§f")
                .addScore(3, "§6mc.quillcraft.fr"));

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
