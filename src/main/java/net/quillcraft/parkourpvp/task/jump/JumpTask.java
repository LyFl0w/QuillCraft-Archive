package net.quillcraft.parkourpvp.task.jump;

import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.scoreboard.JumpScoreboard;
import net.quillcraft.parkourpvp.status.InGameStatus;
import net.quillcraft.parkourpvp.task.jump.wait.after.WaitAfterJumpTaskManager;

public class JumpTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;
    private final int timeToReach;
    private long startedAtTimeMillis;

    public JumpTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.time = 0;
        this.timeToReach = 300; // 60s*5 = 5min
        this.parkourPvP = ((JumpTaskManager)customTaskManager).getJavaPlugin();
        updateCurrentTimeMillis();
    }

    @Override
    public void run(){
        new JumpScoreboard(parkourPvP).updateTime();

        if(time == timeToReach-10 || time >= timeToReach-5 && time != timeToReach){
            final int seconde = (timeToReach-time)%60;
            parkourPvP.getServer().broadcastMessage("§cIl reste "+seconde+" seconde"+((seconde > 1) ? "s" : ""));
        }

        if(time == timeToReach){
            //END Jump phase
            parkourPvP.getGameManager().setInGameStatus(InGameStatus.WAITING_AFTER_JUMP);

            parkourPvP.getServer().broadcastMessage("\n§ctemps écoulé !");

            ((WaitAfterJumpTaskManager)TaskManager.WAIT_AFTER_JUMP_TASK_MANAGER.getCustomTaskManager()).startDefaultTask();

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

    public long getStartedAtTimeMillis(){
        return startedAtTimeMillis;
    }

    protected void updateCurrentTimeMillis(){
        this.startedAtTimeMillis = System.currentTimeMillis();
    }
}
