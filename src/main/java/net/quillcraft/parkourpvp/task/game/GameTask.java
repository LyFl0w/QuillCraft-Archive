package net.quillcraft.parkourpvp.task.game;

import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.scoreboard.GameScoreboard;

public class GameTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;
    private final int timeToReach;

    public GameTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.time = 0;
        this.timeToReach = 300; // 60s*5 = 5min
        this.parkourPvP = ((GameTaskManager)customTaskManager).getJavaPlugin();
    }

    @Override
    public void run(){

        GameScoreboard.updateTime(parkourPvP);

        if(time == timeToReach){
            //END Jump phase
            parkourPvP.getServer().broadcastMessage("FIN DU PARKOUR");
            cancel();
            return;
        }

        if(time%60 == 0 && time != 0) parkourPvP.getServer().broadcastMessage("Il reste "+(timeToReach-time)/60+" min");

        time++;
    }



    public int getTime(){
        return time;
    }

    public void setTime(int time){
        this.time = time;
    }

    public int getTimeToReach(){
        return timeToReach;
    }
}
