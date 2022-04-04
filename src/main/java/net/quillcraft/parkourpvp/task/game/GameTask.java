package net.quillcraft.parkourpvp.task.game;

import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.scoreboard.GameScoreboard;

public class GameTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;
    private final int timeToReach;
    private final long timeToWait;

    public GameTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.time = 0;
        this.timeToReach = 300; // 60s*5 = 5min
        this.parkourPvP = ((GameTaskManager)customTaskManager).getJavaPlugin();
        this.timeToWait = System.currentTimeMillis() + timeToReach * 1000;
    }

    @Override
    public void run(){

        new GameScoreboard(parkourPvP).updateTime();

        if(time%60 == 0 && time != 0) parkourPvP.getServer().broadcastMessage("Il reste "+(timeToReach-time)/60+" min");

        if(time == timeToReach){
            //END Jump phase
            parkourPvP.getServer().broadcastMessage("FIN DU PARKOUR");
            cancel();
        }

        time++;
    }

    public int getTime(){
        return time;
    }

    public void setTime(int time){
        this.time = time;
    }

    public long getTimeToWait(){
        return timeToWait;
    }
}
