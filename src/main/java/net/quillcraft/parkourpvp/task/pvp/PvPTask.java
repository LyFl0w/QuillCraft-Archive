package net.quillcraft.parkourpvp.task.pvp;

import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;

public class PvPTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;

    public PvPTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.parkourPvP = ((PvPTaskManager)customTaskManager).getJavaPlugin();
        this.time = 180;
    }

    @Override
    public void run(){

        if(time == 0){

            cancel();
            return;
        }

        time--;
    }

}
