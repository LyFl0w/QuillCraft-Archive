package net.quillcraft.commons.game.waiter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Waiter {

    private UUID playerUUID;
    private boolean hasParty;
    private long time;

    //For redis
    private Waiter() {}

    public Waiter(UUID playerUUID, boolean hasParty) {
        this.playerUUID = playerUUID;
        this.hasParty = hasParty;
        this.time = System.currentTimeMillis();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean hasParty() {
        return hasParty;
    }

    /**
     * The getPower method is used to sort the waiting list.
     * The higher the rank and the longer the player waits, the greater the power.
     * <p>Exemple rank -> : level 1 : 0 power, level 2 : 1 power, level 3 : 2 power, ...</p>
     * <p>Exemple time -> : <= 30 sec : 0 power, <= 1 min : 1 power, <= 1min30 : 2 power, ...</p>
     *
     * @return int power
     */
    public int getPower() {
        //TODO : RANKING SYSTEM
        final int playerRank = 0;
        return playerRank+getWaitingPower();
    }

    private int getWaitingPower() {
        int power = 5;
        final long waitTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-time);
        if(waitTime < 300) {
            for(int i = 30; i <= 180; i += 30) {
                if(waitTime >= i) continue;
                power = (i-30)/30;
                break;
            }
            return power;
        }
        return 10;
    }
}
