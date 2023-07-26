package net.quillcraft.commons.game.statistiques.parkourpvp;

import net.quillcraft.commons.game.statistiques.PlayerGameData;

import java.text.DecimalFormat;
import java.util.UUID;

public class PlayerParkourPvPData extends PlayerGameData {

    private int checkPointID, jump, kill, respawn, coins;
    private boolean hasFinishParkour, isDead, isBestKiller, isWorstKiller;
    private long timeToFinishParkour;

    public PlayerParkourPvPData(UUID uuid, String playerName, String mapName) {
        this(uuid, playerName, mapName, 0, 0, 0, 0, 0, false, false, false, false, false, -1L);
    }

    public PlayerParkourPvPData(UUID uuid, String playerName, String mapName, int checkPointID, int jump, int kill, int respawn, int coins, boolean hasWin, boolean hasFinishParkour, boolean isDead, boolean isBestKiller, boolean isWorstKiller, long timeToFinishParkour) {
        super(uuid, playerName, mapName, hasWin);
        this.checkPointID = checkPointID;
        this.jump = jump;
        this.kill = kill;
        this.respawn = respawn;
        this.hasFinishParkour = hasFinishParkour;
        this.isBestKiller = isBestKiller;
        this.isWorstKiller = isWorstKiller;
        this.isDead = isDead;
        this.timeToFinishParkour = timeToFinishParkour;
        this.coins = coins;
    }

    public int getCheckPointID() {
        return checkPointID;
    }

    public void setCheckPointID(int checkPointID) {
        this.checkPointID = checkPointID;
    }

    public int getJump() {
        return jump;
    }

    public int getKill() {
        return kill;
    }

    public int getRespawn() {
        return respawn;
    }

    public int getCoins() {
        return coins;
    }

    public boolean hasFinishParkour() {
        return hasFinishParkour;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isBestKiller() {
        return isBestKiller;
    }

    public boolean isWorstKiller() {
        return isWorstKiller;
    }

    public long getTimeToFinishParkour() {
        return timeToFinishParkour;
    }

    public void setTimeToFinishParkour(long timeToFinishParkour) {
        this.hasFinishParkour = true;
        this.timeToFinishParkour = timeToFinishParkour;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    public void setWin() {
        this.hasWin = true;
    }

    public void setDead() {
        this.isDead = true;
    }

    public void setBestKiller() {
        isBestKiller = true;
    }

    public void setWorstKiller() {
        isWorstKiller = true;
    }

    public void addJump() {
        this.jump++;
    }

    public void addRespawn() {
        this.respawn++;
    }

    public void addKill() {
        this.kill++;
    }

    public StringBuilder getFormatedTimeToFinishParkour(StringBuilder stringBuilder) {
        final float timeToFinishParkourSeconde = getTimeToFinishParkour()/1000f;
        final int minute = (int) (timeToFinishParkourSeconde/60);
        final float seconde = timeToFinishParkourSeconde%60;

        if(minute > 0) stringBuilder.append(minute).append(" minute").append((minute > 1) ? "s" : "").append(" et ");
        stringBuilder.append(new DecimalFormat("##.###").format(seconde)).append(" seconde").append((seconde > 1) ? "s" : "");

        return stringBuilder;
    }

}
