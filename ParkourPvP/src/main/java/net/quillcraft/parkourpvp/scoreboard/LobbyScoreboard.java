package net.quillcraft.parkourpvp.scoreboard;

import net.quillcraft.commons.game.GameEnum;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.commons.game.statistiques.PlayerGameStatistiqueProvider;
import net.quillcraft.commons.game.statistiques.parkourpvp.PlayerParkourPvPStatistique;
import net.quillcraft.core.manager.ScoreboardManager;
import net.quillcraft.core.utils.builders.scoreboard.ObjectiveBuilder;
import net.quillcraft.core.utils.builders.scoreboard.ScoreboardBuilder;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.manager.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.text.DecimalFormat;

public class LobbyScoreboard implements ScoreboardManager {

    // TODO : PUT SCOREBOARD MANAGER FROM QuillCraftLobby INTO QuillCraftCore


    // FIXME: 20/02/2022 REMOVE LOBBY FOR WAITING SALLE (LOBBY IS JUST FOR THE BETA)

    private final ParkourPvP parkourPvP;
    public LobbyScoreboard(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    //TODO : SPECTATOR SCOREBOARD IN GAME (SAME AS GameScoreboard but without some lines)

    public void setScoreboard(Player player){
        final GameManager gameManager = parkourPvP.getGameManager();
        final ScoreboardBuilder scoreboardBuilder = new ScoreboardBuilder(parkourPvP);
        final PlayerParkourPvPStatistique playerParkourPvPStatistique = new PlayerGameStatistiqueProvider<PlayerParkourPvPStatistique>(parkourPvP, player.getUniqueId(), GameEnum.PARKOUR_PVP_SOLO).getPlayerData();

        final String mapName = gameManager.getDefaultWorldName();
        final long timeToFinishParkour = playerParkourPvPStatistique.getBestTimeToFinishParkourByMap().getOrDefault(mapName, -1L);
        final ObjectiveBuilder objectiveBuilder = new ObjectiveBuilder("sbs", "§lParkourPvP", DisplaySlot.SIDEBAR)
                .addScore(14, "§a")
                .addScore(13, "§b§lStats")
                .addScore(11, "Total Jumps: "+playerParkourPvPStatistique.getTotalJump())
                .addScore(10, "Total Parcours Fini: "+playerParkourPvPStatistique.getTotalParkourFinish())
                .addScore(9, "§b")
                .addScore(8, "Total Kills: "+playerParkourPvPStatistique.getTotalKills())
                .addScore(7, "Total Victoires: "+playerParkourPvPStatistique.getTotalWin())
                .addScore(6, "§c")
                .addScore(5, getPlayerSizeLine())
                .addScore(4, "§d")
                .addScore(3, "Map : "+mapName)
                .addScore(2, "§e")
                .addScore(1, "§6mc.quillcraft.fr");

        if(timeToFinishParkour != -1L) objectiveBuilder.addScore(12, "Meilleur Temps : "+getFormatedBestTimeToFinishParkour(timeToFinishParkour));

        scoreboardBuilder.addObjective(objectiveBuilder);

        scoreboardBuilder.addPlayer(player);
        scoreboardBuilder.updateScoreboard();

        gameManager.getScoreboardBuilderHashMap().put(player.getName(), scoreboardBuilder);
    }

    public void updatePlayersSize(){
        final String newLine = getPlayerSizeLine();
        parkourPvP.getGameManager().getScoreboardBuilderHashMap().values().stream().parallel()
                .forEach(scoreboardBuilder -> scoreboardBuilder.updateScore("sbs", 5, newLine).updateScoreboard());
    }

    private String getPlayerSizeLine(){
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();
        return "Joueurs : §a"+parkourPvPGame.getPlayerUUIDList().size()+"/"+parkourPvPGame.getGameProperties().getMaxPlayer();
    }

    private String getFormatedBestTimeToFinishParkour(long time) {
        final StringBuilder stringBuilder = new StringBuilder();
        final float timeToFinishParkourSeconde = (float)time / 1000.0F;
        final int minute = (int)(timeToFinishParkourSeconde / 60.0F);
        final float seconde = timeToFinishParkourSeconde % 60.0F;

        if (minute > 0) {
            stringBuilder.append(minute).append("m ");
        }

        stringBuilder.append((new DecimalFormat("##.#")).format(seconde)).append("s");
        return stringBuilder.toString();
    }

}
