package net.quillcraft.parkourpvp.manager;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.task.end.EndTaskManager;
import net.quillcraft.parkourpvp.task.jump.JumpTaskManager;
import net.quillcraft.parkourpvp.task.jump.wait.after.WaitAfterJumpTaskManager;
import net.quillcraft.parkourpvp.task.jump.wait.before.WaitBeforeJumpTaskManager;
import net.quillcraft.parkourpvp.task.lobby.LobbyTaskManager;
import net.quillcraft.parkourpvp.task.pvp.PvPTaskManager;
import net.quillcraft.parkourpvp.task.pvp.wait.before.WaitBeforePvPTaskManager;

public enum TaskManager{

    LOBBY_TASK_MANAGER(new LobbyTaskManager(ParkourPvP.getInstance())),

    WAIT_BEFORE_JUMP_TASK_MANAGER(new WaitBeforeJumpTaskManager(ParkourPvP.getInstance())),
    JUMP_TASK_MANAGER(new JumpTaskManager(ParkourPvP.getInstance())),
    WAIT_AFTER_JUMP_TASK_MANAGER(new WaitAfterJumpTaskManager(ParkourPvP.getInstance())),

    WAIT_BEFORE_PVP_TASK_MANAGER(new WaitBeforePvPTaskManager(ParkourPvP.getInstance())),
    PVP_TASK_MANAGER(new PvPTaskManager(ParkourPvP.getInstance())),

    END_TASK_MANAGER(new EndTaskManager(ParkourPvP.getInstance()));

    private final CustomTaskManager customTaskManager;
    TaskManager(CustomTaskManager customTaskManager){
        this.customTaskManager = customTaskManager;
    }

    public CustomTaskManager getCustomTaskManager(){
        return customTaskManager;
    }
}
