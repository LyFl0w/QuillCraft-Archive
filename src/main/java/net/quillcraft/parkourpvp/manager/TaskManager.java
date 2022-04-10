package net.quillcraft.parkourpvp.manager;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.task.jump.JumpTaskManager;
import net.quillcraft.parkourpvp.task.jumpwait.WaitJumpTaskManager;
import net.quillcraft.parkourpvp.task.wait.LobbyTaskManager;

public enum TaskManager{

    LOBBY_TASK_MANAGER(new LobbyTaskManager(ParkourPvP.getInstance())),
    WAIT_JUMP_TASK_MANAGER(new WaitJumpTaskManager(ParkourPvP.getInstance())),
    JUMP_TASK_MANAGER(new JumpTaskManager(ParkourPvP.getInstance()));

    private final CustomTaskManager customTaskManager;
    TaskManager(CustomTaskManager customTaskManager){
        this.customTaskManager = customTaskManager;
    }

    public CustomTaskManager getCustomTaskManager(){
        return customTaskManager;
    }
}
