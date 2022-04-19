package net.quillcraft.parkourpvp.game;

import java.util.UUID;

public class PlayerData{

    private final UUID uuid;
    private int checkPointID, jump, kill, coins;
    private boolean hasWin, hasFinishParkour, isDead;
    private long timeToFinishParkour;

    public PlayerData(UUID uuid, int checkPointID, int jump, int kill, int coins, boolean hasWin, boolean hasFinishParkour, boolean isDead, long timeToFinishParkour){
        this.uuid = uuid;
        this.checkPointID = checkPointID;
        this.jump = jump;
        this.kill = kill;
        this.hasWin = hasWin;
        this.hasFinishParkour = hasFinishParkour;
        this.isDead = isDead;
        this.timeToFinishParkour = timeToFinishParkour;
        this.coins = coins;
    }

    public PlayerData(UUID uuid){
        this(uuid, 0, 0, 0, 0, false, false, false, -1L);
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

    public int getKill(){
        return kill;
    }

    public int getCoins(){
        return coins;
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

    public void setFinishParkour(){
        this.hasFinishParkour = true;
    }

    private void addJump(){
        this.jump++;
    }

    private void addKill(){
        this.kill++;
    }

    public void addCoins(int toAdd){
        this.coins += toAdd;
    }

    public void removeCoins(int toRemove){
        this.coins -= toRemove;
    }

}
