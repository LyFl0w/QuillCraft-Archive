package net.quillcraft.parkourpvp.task.pvp.wait.before;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;


public class WaitBeforePvPTaskManager extends CustomTaskManager{

    public WaitBeforePvPTaskManager(ParkourPvP parkourPvP){
        super(parkourPvP, WaitBeforePvPTask.class);
    }

    @Override
    public WaitBeforePvPTask getTask(){
        return (WaitBeforePvPTask) super.getTask();
    }

    @Override @SuppressWarnings("unchecked")
    protected ParkourPvP getJavaPlugin(){
        return (ParkourPvP) javaPlugin;
    }

}
