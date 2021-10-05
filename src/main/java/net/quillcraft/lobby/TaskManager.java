package net.quillcraft.lobby;

import net.quillcraft.lobby.task.AutoMessageTask;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private final QuillCraftLobby quillCraftLobby;
    private final List<BukkitTask> taskInProcess;
    public TaskManager(QuillCraftLobby quillCraftLobby){
        this.quillCraftLobby = quillCraftLobby;
        this.taskInProcess = new ArrayList<>();
        onEnableTasks();
    }

    private void onEnableTasks(){
        taskInProcess.add(new AutoMessageTask(quillCraftLobby)
                .runTaskTimerAsynchronously(quillCraftLobby, 120L, 20L*60));
    }

    public void onDisableTasks(){
        taskInProcess.forEach(BukkitTask::cancel);
    }


}
