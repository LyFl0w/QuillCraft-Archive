package net.quillcraft.parkourpvp.task;

import net.quillcraft.commons.game.ParkourPvPGame;

import org.bukkit.scheduler.BukkitRunnable;

public class StartingTask extends BukkitRunnable{

    private int time;
    private final int defaultTime;

    private final ParkourPvPGame parkourPvPGame;

    public StartingTask(ParkourPvPGame parkourPvPGame, int time){
        this.parkourPvPGame = parkourPvPGame;
        this.defaultTime = time;
        this.time = time;
    }

    @Override
    public void run(){
        if(time == 0){

            return;
        }
        time--;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException{
        super.cancel();

        this.time = defaultTime;
    }
}
