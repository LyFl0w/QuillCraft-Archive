package net.quillcraft.core.task;

import net.quillcraft.core.exception.TaskOverflowException;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class CustomTaskManager{

    protected final JavaPlugin javaPlugin;
    private final Class<? extends CustomTask> taskModel;
    protected CustomTask task;
    protected boolean isRunning;

    public CustomTaskManager(JavaPlugin javaPlugin, Class<? extends CustomTask> taskModel){
        this.javaPlugin = javaPlugin;
        this.taskModel = taskModel;
        this.isRunning = false;
        resetTask();
    }

    public void runTaskTimer(long start, long delay) throws TaskOverflowException{
        if(isRunning) throw new TaskOverflowException();
        task.runTaskTimer(javaPlugin, start, delay);
        isRunning = true;
    }

    public void runTaskLater(long delay) throws TaskOverflowException{
        if(isRunning) throw new TaskOverflowException();
        task.runTaskLater(javaPlugin, delay);
        isRunning = true;
    }

    protected void resetTask(){
        try{
            task = taskModel.getConstructor(CustomTaskManager.class).newInstance(this);
        }catch(Exception exception){
            javaPlugin.getLogger().severe(exception.getMessage());
        }
    }

    public void cancel(){
        if(!isRunning) return;
        if(!task.isCancelled()) task.cancel();
    }

    public boolean isRunning(){
        return isRunning;
    }

    public CustomTask getTask(){
        return task;
    }

    protected abstract <T extends JavaPlugin> T getJavaPlugin();
}
