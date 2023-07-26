package net.quillcraft.commons.game.statistiques.parkourpvp;

import net.quillcraft.commons.game.statistiques.PlayerGameData;
import net.quillcraft.commons.game.statistiques.PlayerGameStatistique;

import java.util.HashMap;

public class PlayerParkourPvPStatistique extends PlayerGameStatistique {

    private final HashMap<String, Integer> totalKillByMap, totalRespawnByMap, totalFinishParkourByMap, totalDeathByMap, totalBestKillerByMap, totalWorstKillerByMap, totalJumpByMap, bestKillByMap;
    private final HashMap<String, Long> bestTimeToFinishParkourByMap;
    private int totalKills, totalDeaths, totalRespawn, totalJump, totalBestKiller, totalWorstKiller, totalParkourFinish;

    public PlayerParkourPvPStatistique() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    protected PlayerParkourPvPStatistique(HashMap<String, Integer> totalGamesPlayedByMap, HashMap<String, Integer> totalWinByMap, HashMap<String, Integer> totalKillByMap, HashMap<String, Integer> totalRespawnByMap, HashMap<String, Integer> totalFinishParkourByMap, HashMap<String, Integer> totalDeathByMap, HashMap<String, Integer> totalBestKillerByMap, HashMap<String, Integer> totalWorstKillerByMap, HashMap<String, Integer> totalJumpByMap, HashMap<String, Integer> bestKillByMap, HashMap<String, Long> bestTimeToFinishParkourByMap, int totalGamePlayed, int totalKills, int totalDeaths, int totalWin, int totalRespawn, int totalJump, int totalBestKiller, int totalWorstKiller, int totalParkourFinish) {
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

    private void addSimpleStatsTime(HashMap<String, Long> timeHashMap, String mapName, long newTime) {
        if(!timeHashMap.containsKey(mapName)) {
            timeHashMap.put(mapName, newTime);
            return;
        }
        final long actualTime = timeHashMap.get(mapName);
        if((actualTime != -1) && actualTime > newTime) timeHashMap.put(mapName, newTime);
    }

    private void setBestStats(HashMap<String, Integer> timeHashMap, String mapName, int dataToAdd) {
        if(!timeHashMap.containsKey(mapName)) {
            timeHashMap.put(mapName, dataToAdd);
            return;
        }
        final long actualData = timeHashMap.get(mapName);
        if(actualData < dataToAdd) timeHashMap.put(mapName, dataToAdd);
    }

    public HashMap<String, Integer> getTotalKillByMap() {
        return totalKillByMap;
    }

    public HashMap<String, Integer> getTotalRespawnByMap() {
        return totalRespawnByMap;
    }

    public HashMap<String, Integer> getTotalFinishParkourByMap() {
        return totalFinishParkourByMap;
    }

    public HashMap<String, Integer> getTotalDeathByMap() {
        return totalDeathByMap;
    }

    public HashMap<String, Integer> getTotalBestKillerByMap() {
        return totalBestKillerByMap;
    }

    public HashMap<String, Integer> getTotalWorstKillerByMap() {
        return totalWorstKillerByMap;
    }

    public HashMap<String, Integer> getTotalJumpByMap() {
        return totalJumpByMap;
    }

    public HashMap<String, Long> getBestTimeToFinishParkourByMap() {
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

    public HashMap<String, Integer> getBestKillByMap() {
        return bestKillByMap;
    }
}
