package net.quillcraft.commons.game.statistiques.parkourpvp;

import net.quillcraft.commons.game.statistiques.PlayerGameData;
import net.quillcraft.commons.game.statistiques.PlayerGameStatistique;

import java.util.HashMap;
import java.util.Map;

public class PlayerParkourPvPStatistique extends PlayerGameStatistique {

    private final Map<String, Integer> totalKillByMap;
    private final Map<String, Integer> totalRespawnByMap;
    private final Map<String, Integer> totalFinishParkourByMap;
    private final Map<String, Integer> totalDeathByMap;
    private final Map<String, Integer> totalBestKillerByMap;
    private final Map<String, Integer> totalWorstKillerByMap;
    private final Map<String, Integer> totalJumpByMap;
    private final Map<String, Integer> bestKillByMap;
    private final Map<String, Long> bestTimeToFinishParkourByMap;
    private int totalKills;
    private int totalDeaths;
    private int totalRespawn;
    private int totalJump;
    private int totalBestKiller;
    private int totalWorstKiller;
    private int totalParkourFinish;

    public PlayerParkourPvPStatistique() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    protected PlayerParkourPvPStatistique(Map<String, Integer> totalGamesPlayedByMap, Map<String, Integer> totalWinByMap, Map<String, Integer> totalKillByMap, Map<String, Integer> totalRespawnByMap, Map<String, Integer> totalFinishParkourByMap, Map<String, Integer> totalDeathByMap, Map<String, Integer> totalBestKillerByMap, Map<String, Integer> totalWorstKillerByMap, Map<String, Integer> totalJumpByMap, Map<String, Integer> bestKillByMap, Map<String, Long> bestTimeToFinishParkourByMap, int totalGamePlayed, int totalKills, int totalDeaths, int totalWin, int totalRespawn, int totalJump, int totalBestKiller, int totalWorstKiller, int totalParkourFinish) {
        super(totalGamesPlayedByMap, totalWinByMap, totalGamePlayed, totalWin);
        this.totalKillByMap = totalKillByMap;
        this.totalRespawnByMap = totalRespawnByMap;
        this.totalFinishParkourByMap = totalFinishParkourByMap;
        this.totalDeathByMap = totalDeathByMap;
        this.totalBestKillerByMap = totalBestKillerByMap;
        this.bestTimeToFinishParkourByMap = bestTimeToFinishParkourByMap;
        this.totalWorstKillerByMap = totalWorstKillerByMap;
        this.totalJumpByMap = totalJumpByMap;
        this.bestKillByMap = bestKillByMap;
        this.totalKills = totalKills;
        this.totalDeaths = totalDeaths;
        this.totalRespawn = totalRespawn;
        this.totalJump = totalJump;
        this.totalBestKiller = totalBestKiller;
        this.totalWorstKiller = totalWorstKiller;
        this.totalParkourFinish = totalParkourFinish;
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
        addSimpleStats(totalGamesPlayedByMap, mapName, 1);

        addSimpleStatsTime(bestTimeToFinishParkourByMap, mapName, playerParkourPvPData.getTimeToFinishParkour());

        setBestStats(bestKillByMap, mapName, playerParkourPvPData.getKill());


        totalGamePlayed++;
        totalWin += (playerParkourPvPData.hasWin() ? 1 : 0);
        totalDeaths += (playerParkourPvPData.isDead() ? 1 : 0);
        totalBestKiller += (playerParkourPvPData.isBestKiller() ? 1 : 0);
        totalWorstKiller += (playerParkourPvPData.isWorstKiller() ? 1 : 0);
        totalParkourFinish += (playerParkourPvPData.hasFinishParkour() ? 1 : 0);
        totalKills += playerParkourPvPData.getKill();
        totalRespawn += playerParkourPvPData.getRespawn();
        totalJump += playerParkourPvPData.getJump();
    }

    private void addSimpleStatsTime(Map<String, Long> timeHashMap, String mapName, long newTime) {
        if (timeHashMap.putIfAbsent(mapName, newTime) != null) return;

        final long actualTime = timeHashMap.get(mapName);
        if ((actualTime != -1) && actualTime > newTime) timeHashMap.put(mapName, newTime);
    }

    private void setBestStats(Map<String, Integer> timeHashMap, String mapName, int dataToAdd) {
        if (timeHashMap.putIfAbsent(mapName, dataToAdd) != null) return;

        final long actualData = timeHashMap.get(mapName);
        if (actualData < dataToAdd) timeHashMap.put(mapName, dataToAdd);
    }

    public Map<String, Integer> getTotalKillByMap() {
        return totalKillByMap;
    }

    public Map<String, Integer> getTotalRespawnByMap() {
        return totalRespawnByMap;
    }

    public Map<String, Integer> getTotalFinishParkourByMap() {
        return totalFinishParkourByMap;
    }

    public Map<String, Integer> getTotalDeathByMap() {
        return totalDeathByMap;
    }

    public Map<String, Integer> getTotalBestKillerByMap() {
        return totalBestKillerByMap;
    }

    public Map<String, Integer> getTotalWorstKillerByMap() {
        return totalWorstKillerByMap;
    }

    public Map<String, Integer> getTotalJumpByMap() {
        return totalJumpByMap;
    }

    public Map<String, Long> getBestTimeToFinishParkourByMap() {
        return bestTimeToFinishParkourByMap;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public int getTotalRespawn() {
        return totalRespawn;
    }

    public int getTotalJump() {
        return totalJump;
    }

    public int getTotalBestKiller() {
        return totalBestKiller;
    }

    public int getTotalWorstKiller() {
        return totalWorstKiller;
    }

    public int getTotalParkourFinish() {
        return totalParkourFinish;
    }

    public Map<String, Integer> getBestKillByMap() {
        return bestKillByMap;
    }
}
