package net.quillcraft.parkourpvp.game;

import java.text.DecimalFormat;
import java.util.UUID;

public class PlayerDataGame{

    private final String mapName;
    private final UUID uuid;
    private int checkPointID, jump, kill, respawn, coins;
    private boolean hasWin, hasFinishParkour, isDead;
    private long timeToFinishParkour;

    public PlayerDataGame(UUID uuid, String mapName, int checkPointID, int jump, int kill, int respawn, int coins, boolean hasWin, boolean hasFinishParkour, boolean isDead, long timeToFinishParkour){
        this.uuid = uuid;
        this.mapName = mapName;
        this.checkPointID = checkPointID;
        this.jump = jump;
        this.kill = kill;
        this.respawn = respawn;
        this.hasWin = hasWin;
        this.hasFinishParkour = hasFinishParkour;
        this.isDead = isDead;
        this.timeToFinishParkour = timeToFinishParkour;
        this.coins = coins;
    }

    public PlayerDataGame(UUID uuid, String mapName){
        this(uuid, mapName,0, 0, 0, 0, 0, false, false, false, -1L);
    }

    public UUID getUuid(){
        return uuid;
    }

    public int getCheckPointID(){
        return checkPointID;
    }

    public int getJump(){
        return jump;
    }

    public int getRespawn(){
        return respawn;
    }

    public int getKill(){
        return kill;
    }

    public int getCoins(){
        return coins;
    }

    public String getMapName(){
        return mapName;
    }

    public boolean hasWin(){
        return hasWin;
    }

    public boolean hasFinishParkour(){
        return hasFinishParkour;
    }

    public boolean isDead(){
        return isDead;
    }

    public long getTimeToFinishParkour(){
        return timeToFinishParkour;
    }

    public void setTimeToFinishParkour(long timeToFinishParkour){
        this.hasFinishParkour = true;
        this.timeToFinishParkour = timeToFinishParkour;
    }

    public void setCheckPointID(int checkPointID){
        this.checkPointID = checkPointID;
    }

    public void setWin(){
        this.hasWin = true;
    }

    public void setDead(){
        this.isDead = true;
    }

    public void addJump(){
        this.jump++;
    }

    public void addRespawn(){
        this.respawn++;
    }

    public void addKill(){
        this.kill++;
    }

    public void addCoins(int toAdd){
        this.coins += toAdd;
    }

    public void removeCoins(int toRemove){
        this.coins -= toRemove;
    }

    public StringBuilder getFormatedTimeToFinishParkour(StringBuilder stringBuilder){
        final float timeToFinishParkourSeconde = getTimeToFinishParkour() / 1000f;
        final int minute = (int) (timeToFinishParkourSeconde / 60);
        final float seconde = timeToFinishParkourSeconde % 60;

        if(minute > 0) stringBuilder.append(minute).append(" minute").append((minute > 1) ? "s" : "").append(" et ");
        stringBuilder.append(new DecimalFormat("##.###").format(seconde)).append(" seconde").append((seconde > 1) ? "s" : "");

        return stringBuilder;
    }
}
