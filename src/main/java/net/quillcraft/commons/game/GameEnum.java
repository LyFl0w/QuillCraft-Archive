package net.quillcraft.commons.game;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public enum GameEnum{

    PARKOUR_PVP_SOLO("Parkour PvP (solo)");

    private final String gameName;

    GameEnum(String gameName){
        this.gameName = gameName;
    }

    public String getGameName(){
        return gameName;
    }

    public static void removePlayerWaiting(UUID uuid){
        Arrays.stream(GameEnum.values()).parallel().forEach(gameEnum -> {
            final WaitingList waitingList = new WaitingList(gameEnum);
            final List<Waiter> waiterList = waitingList.getWaitersList();
            for(Waiter waiter : waiterList){
                if(waiter.getPlayerUUID().equals(uuid)){
                    waiterList.remove(waiter);
                    waitingList.updateWaitersListRedis();
                    return;
                }
            }
        });
    }

    @Nullable
    public static GameEnum getGamePlayerWaiting(UUID uuid){
        for(GameEnum gameEnum : values()){
            final List<Waiter> waiterList = new WaitingList(gameEnum).getWaitersList();
            if(waiterList.stream().anyMatch(waiter -> waiter.getPlayerUUID() == uuid)) return gameEnum;
        }
        return null;
    }
}
