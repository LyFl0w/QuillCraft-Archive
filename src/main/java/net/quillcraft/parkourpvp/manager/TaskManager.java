package net.quillcraft.parkourpvp.manager;

import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.task.StartingTask;

import org.bukkit.scheduler.BukkitRunnable;

public enum TaskManager{

    STARTING(new StartingTask(ParkourPvP.getInstance().getParkourPvPGame(), 15));

    private final BukkitRunnable bukkitRunnable;
    TaskManager(BukkitRunnable bukkitRunnable){
        this.bukkitRunnable = bukkitRunnable;
    }

    public BukkitRunnable getBukkitRunnable(){
        return bukkitRunnable;
    }
}
