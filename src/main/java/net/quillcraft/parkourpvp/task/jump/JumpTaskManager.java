package net.quillcraft.parkourpvp.task.jump;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;

public class JumpTaskManager extends CustomTaskManager{

    public JumpTaskManager(ParkourPvP parkourPvP){
        super(parkourPvP, JumpTask.class);
    }

    @Override
    public JumpTask getTask(){
        return (JumpTask) super.getTask();
    }

    @Override @SuppressWarnings("unchecked")
    protected ParkourPvP getJavaPlugin(){
        return (ParkourPvP) javaPlugin;
    }

    public JumpTaskManager setStartedAtTimeMillis(){
        getTask().updateCurrentTimeMillis();
        return this;
    }
}
