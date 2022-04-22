package net.quillcraft.parkourpvp.task.end;

import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;

public class EndTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;

    public EndTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.parkourPvP = ((EndTaskManager)customTaskManager).getJavaPlugin();
        this.time = 0;
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
