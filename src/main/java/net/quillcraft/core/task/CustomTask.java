package net.quillcraft.core.task;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class CustomTask extends BukkitRunnable {

    private final CustomTaskManager customTaskManager;

    protected CustomTask(CustomTaskManager customTaskManager) {
        this.customTaskManager = customTaskManager;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();

        customTaskManager.isRunning = false;
        customTaskManager.resetTask();
    }

}
