package net.quillcraft.commons.game.statistiques.parkourpvp;

import net.quillcraft.commons.game.statistiques.PlayerGameData;
import net.quillcraft.commons.game.statistiques.PlayerGameStatistique;

import java.util.HashMap;

public class PlayerParkourPvPStatistique extends PlayerGameStatistique {

    private final HashMap<String, Integer> totalKillByMap, totalRespawnByMap, totalFinishParkourByMap, totalDeathByMap, totalBestKillerByMap, totalWorstKillerByMap, totalJumpByMap;
    private final HashMap<String, Long> timeToFinishParkourByMap, bestFinishParkourByMap;
    private int totalKills, totalDeaths, totalRespawn, totalJump, totalBestKiller, totalWorstKiller;

    private PlayerParkourPvPStatistique() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), 0, 0, 0, 0, 0, 0, 0, 0);
    }

    protected PlayerParkourPvPStatistique(HashMap<String, Integer> totalGamesPlayedByMap, HashMap<String, Integer> totalWinByMap, HashMap<String, Integer> totalKillByMap, HashMap<String, Integer> totalRespawnByMap, HashMap<String, Integer> totalFinishParkourByMap, HashMap<String, Integer> totalDeathByMap, HashMap<String, Integer> totalBestKillerByMap, HashMap<String, Integer> totalWorstKillerByMap, HashMap<String, Integer> totalJumpByMap, HashMap<String, Long> timeToFinishParkourByMap, HashMap<String, Long> bestFinishParkourByMap, int totalGamePlayed, int totalKills, int totalDeaths, int totalWin, int totalRespawn, int totalJump, int totalBestKiller, int totalWorstKiller) {
        super(totalGamesPlayedByMap, totalWinByMap, totalGamePlayed, totalWin);
        this.totalKillByMap = totalKillByMap;
        this.totalRespawnByMap = totalRespawnByMap;
        this.totalFinishParkourByMap = totalFinishParkourByMap;
        this.totalDeathByMap = totalDeathByMap;
        this.totalBestKillerByMap = totalBestKillerByMap;
        this.bestFinishParkourByMap = bestFinishParkourByMap;
        this.totalWorstKillerByMap = totalWorstKillerByMap;
        this.totalJumpByMap = totalJumpByMap;
        this.timeToFinishParkourByMap = timeToFinishParkourByMap;
        this.totalKills = totalKills;
        this.totalDeaths = totalDeaths;
        this.totalRespawn = totalRespawn;
        this.totalJump = totalJump;
        this.totalBestKiller = totalBestKiller;
        this.totalWorstKiller = totalWorstKiller;
    }

    @Override
    public <T extends PlayerGameData> void addStatistiques(String mapName, T playerGameData) {
        final PlayerParkourPvPData playerParkourPvPData = (PlayerParkourPvPData) playerGameData;

        addSimpleStats(totalFinishParkourByMap, mapName, playerParkourPvPData.hasFinishParkour());
        addSimpleStats(totalWinByMap, mapName, playerParkourPvPData.hasWin());
        addSimpleStats(totalDeathByMap, mapName, playerParkourPvPData.isDead());
        addSimpleStats(totalBestKillerByMap, mapName, playerParkourPvPData.isBestKiller());
        addSimpleStats(totalWorstKillerByMap, mapName, playerParkourPvPData.isWorstKiller());
        addSimpleStats(totalKillByMap, mapName, playerParkourPvPData.getKill());
        addSimpleStats(totalJumpByMap, mapName, playerParkourPvPData.getJump());
        addSimpleStats(totalRespawnByMap, mapName, playerParkourPvPData.getRespawn());
        addSimpleStats(timeToFinishParkourByMap, mapName, playerParkourPvPData.getTimeToFinishParkour());
        addSimpleStats(bestFinishParkourByMap, mapName, playerParkourPvPData.getTimeToFinishParkour());
        addSimpleStats(totalGamesPlayedByMap, mapName, 1);

        totalGamePlayed++;
        totalWin += (playerParkourPvPData.hasWin() ? 1 : 0);
        totalDeaths += (playerParkourPvPData.isDead() ? 1 : 0);
        totalBestKiller += (playerParkourPvPData.isBestKiller() ? 1 : 0);
        totalWorstKiller += (playerParkourPvPData.isWorstKiller() ? 1 : 0);
        totalKills += playerParkourPvPData.getKill();
        totalRespawn += playerParkourPvPData.getRespawn();
        totalJump += playerParkourPvPData.getJump();
    }

    private void addSimpleStats(HashMap<String, Long> timeHashMap, String mapName, long timeToFinish) {
        if(!timeHashMap.containsKey(mapName)) {
            timeHashMap.put(mapName, timeToFinish);
            return;
        }
        final long time = timeHashMap.get(mapName);
        if((time == -1) || time > timeToFinish) timeHashMap.put(mapName, timeToFinish);
    }
}
