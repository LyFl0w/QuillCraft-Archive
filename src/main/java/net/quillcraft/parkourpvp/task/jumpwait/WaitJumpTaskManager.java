package net.quillcraft.parkourpvp.task.jumpwait;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;

public class WaitJumpTaskManager extends CustomTaskManager{

    public WaitJumpTaskManager(ParkourPvP parkourPvP){
        super(parkourPvP, WaitJumpTask.class);
    }

    @Override
    public WaitJumpTask getTask(){
        return (WaitJumpTask) super.getTask();
    }

    @Override @SuppressWarnings("unchecked")
    protected ParkourPvP getJavaPlugin(){
        return (ParkourPvP) javaPlugin;
    }


}
