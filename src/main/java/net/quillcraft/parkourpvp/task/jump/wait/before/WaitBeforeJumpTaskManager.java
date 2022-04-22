package net.quillcraft.parkourpvp.task.jump.wait.before;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;

public class WaitBeforeJumpTaskManager extends CustomTaskManager{

    public WaitBeforeJumpTaskManager(ParkourPvP parkourPvP){
        super(parkourPvP, WaitBeforeJumpTask.class);
    }

    @Override
    public WaitBeforeJumpTask getTask(){
        return (WaitBeforeJumpTask) super.getTask();
    }

    @Override @SuppressWarnings("unchecked")
    protected ParkourPvP getJavaPlugin(){
        return (ParkourPvP) javaPlugin;
    }


}
