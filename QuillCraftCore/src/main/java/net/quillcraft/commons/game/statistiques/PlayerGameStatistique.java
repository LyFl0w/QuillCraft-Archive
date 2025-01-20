package net.quillcraft.commons.game.statistiques;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerGameStatistique {

    protected Map<String, Integer> totalGamesPlayedByMap;
    protected Map<String, Integer> totalWinByMap;
    protected int totalGamePlayed;
    protected int totalWin;

    protected PlayerGameStatistique() {
        this(new HashMap<>(), new HashMap<>(), 0, 0);
    }

    protected PlayerGameStatistique(Map<String, Integer> totalGamesPlayedByMap, Map<String, Integer> totalWinByMap, int totalGamePlayed, int totalWin) {
        this.totalGamesPlayedByMap = totalGamesPlayedByMap;
        this.totalWinByMap = totalWinByMap;
        this.totalGamePlayed = totalGamePlayed;
        this.totalWin = totalWin;
    }

    public abstract <T extends PlayerGameData> void addStatistiques(String mapName, T playerGameData);

    protected void addSimpleStats(Map<String, Integer> map, String mapName, int value) {
        map.put(mapName, map.getOrDefault(mapName, 0) + value);
    }

    protected void addSimpleStats(Map<String, Integer> map, String mapName, boolean value) {
        map.put(mapName, map.getOrDefault(mapName, 0) + (value ? 1 : 0));
    }

    public Map<String, Integer> getTotalGamesPlayedByMap() {
        return totalGamesPlayedByMap;
    }

    public Map<String, Integer> getTotalWinByMap() {
        return totalWinByMap;
    }

    public int getTotalGamePlayed() {
        return totalGamePlayed;
    }

    public int getTotalWin() {
        return totalWin;
    }

}
