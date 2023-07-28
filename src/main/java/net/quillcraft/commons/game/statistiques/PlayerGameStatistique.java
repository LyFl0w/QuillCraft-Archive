package net.quillcraft.commons.game.statistiques;

import java.util.HashMap;

public abstract class PlayerGameStatistique {

    protected HashMap<String, Integer> totalGamesPlayedByMap, totalWinByMap;
    protected int totalGamePlayed, totalWin;

    public PlayerGameStatistique() {
        this(new HashMap<>(), new HashMap<>(), 0, 0);
    }

    public PlayerGameStatistique(HashMap<String, Integer> totalGamesPlayedByMap, HashMap<String, Integer> totalWinByMap, int totalGamePlayed, int totalWin) {
        this.totalGamesPlayedByMap = totalGamesPlayedByMap;
        this.totalWinByMap = totalWinByMap;
        this.totalGamePlayed = totalGamePlayed;
        this.totalWin = totalWin;
    }

    public abstract <T extends PlayerGameData> void addStatistiques(String mapName, T playerGameData);

    protected void addSimpleStats(HashMap<String, Integer> hashMap, String mapName, int value) {
        hashMap.put(mapName, hashMap.getOrDefault(mapName, 0)+value);
    }

    protected void addSimpleStats(HashMap<String, Integer> hashMap, String mapName, boolean value) {
        hashMap.put(mapName, hashMap.getOrDefault(mapName, 0)+(value ? 1 : 0));
    }

    public HashMap<String, Integer> getTotalGamesPlayedByMap() {
        return totalGamesPlayedByMap;
    }

    public HashMap<String, Integer> getTotalWinByMap() {
        return totalWinByMap;
    }

    public int getTotalGamePlayed() {
        return totalGamePlayed;
    }

    public int getTotalWin() {
        return totalWin;
    }
}
