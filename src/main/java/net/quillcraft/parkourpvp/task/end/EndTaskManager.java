package net.quillcraft.parkourpvp.task.end;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;

public class EndTaskManager extends CustomTaskManager{

    public EndTaskManager(ParkourPvP parkourPvP){
        super(parkourPvP, EndTask.class);
    }

    @Override
    public EndTask getTask(){
        return (EndTask) super.getTask();
    }

    @Override @SuppressWarnings("unchecked")
    protected ParkourPvP getJavaPlugin(){
        return (ParkourPvP) javaPlugin;
    }

}
