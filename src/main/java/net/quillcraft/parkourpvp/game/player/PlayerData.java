package net.quillcraft.parkourpvp.game.player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData{

    private UUID playerUUID;
    private String key;
    private HashMap<String, Integer> countWinByMap, countKillByMap, countRespawnByMap, countFinishParkourByMap, countDeathByMap, countBestKillerByMap, countWorstKillerByMap;
    private HashMap<String, Long> timeToFinishParkourByMap;


    public PlayerData(UUID playerUUID){
        this.playerUUID = playerUUID;
    }

    /*private PlayerData getPlayerDataFromDatabase(){
    }*/

}
