package net.quillcraft.parkourpvp.task.jumpwait;

import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.core.utils.Title;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.manager.TaskManager;

import org.bukkit.entity.Player;

public class WaitJumpTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;

    public WaitJumpTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.parkourPvP = ((WaitJumpTaskManager)customTaskManager).getJavaPlugin();
        this.time = 15;
    }

    @Override
    public void run(){

        if(time % 5 == 0 && time >= 5){
            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(uuid -> {
                final Player player = parkourPvP.getServer().getPlayer(uuid);
                new Title(player).sendFullTitle(0, 0, 0, "Le jump commence", "dans "+time+" secondes");
            });
        }

        if(time % 5 == 0 || time < 5){
            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(uuid -> {
                final Player player = parkourPvP.getServer().getPlayer(uuid);
                new Title(player).sendActionBar("Le jump commence dans "+time+"'s");
            });
        }

        if(time == 0){

            try{
                //Start Wait Jump timer
                TaskManager.JUMP_TASK_MANAGER.getCustomTaskManager().runTaskTimer(0L, 20L);
            }catch(TaskOverflowException e){
                e.printStackTrace();
            }

            cancel();
            return;
        }

        time--;
    }

}
