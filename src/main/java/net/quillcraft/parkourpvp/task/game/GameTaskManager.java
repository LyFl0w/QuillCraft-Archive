package net.quillcraft.parkourpvp.task.game;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;

public class GameTaskManager extends CustomTaskManager{

    public GameTaskManager(ParkourPvP parkourPvP){
        super(parkourPvP, GameTask.class);
    }

    @Override
    public GameTask getTask(){
        return (GameTask) super.getTask();
    }

    @Override @SuppressWarnings("unchecked")
    protected ParkourPvP getJavaPlugin(){
        return (ParkourPvP) javaPlugin;
    }


}
