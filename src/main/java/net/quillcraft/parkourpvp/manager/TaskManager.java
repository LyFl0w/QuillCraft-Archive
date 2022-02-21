package net.quillcraft.parkourpvp.manager;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.task.wait.LobbyTaskManager;

public enum TaskManager{

    STARTING_TASK_MANAGER(new LobbyTaskManager(ParkourPvP.getInstance()));

    private final CustomTaskManager customTaskManager;
    TaskManager(CustomTaskManager customTaskManager){
        this.customTaskManager = customTaskManager;
    }

    public CustomTaskManager getCustomTaskManager(){
        return customTaskManager;
    }
}
