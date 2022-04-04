package net.quillcraft.parkourpvp.manager;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.task.game.GameTaskManager;
import net.quillcraft.parkourpvp.task.wait.LobbyTaskManager;

public enum TaskManager{

    LOBBY_TASK_MANAGER(new LobbyTaskManager(ParkourPvP.getInstance())),
    GAME_TASK_MANAGER(new GameTaskManager(ParkourPvP.getInstance()));

    private final CustomTaskManager customTaskManager;
    TaskManager(CustomTaskManager customTaskManager){
        this.customTaskManager = customTaskManager;
    }

    public CustomTaskManager getCustomTaskManager(){
        return customTaskManager;
    }
}
