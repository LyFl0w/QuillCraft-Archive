package net.quillcraft.parkourpvp.task.jump;

import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.scoreboard.JumpScoreboard;

public class JumpTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;
    private final int timeToReach;

    public JumpTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.time = 0;
        this.timeToReach = 300; // 60s*5 = 5min
        this.parkourPvP = ((JumpTaskManager)customTaskManager).getJavaPlugin();
    }

    @Override
    public void run(){
        new JumpScoreboard(parkourPvP).updateTime();

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
