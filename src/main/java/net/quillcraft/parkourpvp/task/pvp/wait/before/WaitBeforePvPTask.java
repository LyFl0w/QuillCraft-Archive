package net.quillcraft.parkourpvp.task.pvp.wait.before;

import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.core.utils.Title;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.status.InGameStatus;

import org.bukkit.entity.Player;

public class WaitBeforePvPTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;

    public WaitBeforePvPTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.parkourPvP = ((WaitBeforePvPTaskManager)customTaskManager).getJavaPlugin();
        this.time = 180;
    }

    @Override
    public void run(){

        // Minute by minute
        if(time % 60 == 0 && time >= 60){
            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(uuid -> {
                final Player player = parkourPvP.getServer().getPlayer(uuid);
                final int timeToDraw = time/60;
                new Title(player).sendFullTitle(1, 1, 1, "Le PvP commence", "dans "+timeToDraw+" minute"+((timeToDraw > 1) ? "s" : ""));
            });
        }

        if((time % 5 == 0 && time <= 15) || time < 5 && time > 0){
            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(uuid -> {
                final Player player = parkourPvP.getServer().getPlayer(uuid);
                new Title(player).sendActionBar("Le PvP commence dans "+time+"'s");
            });
        }

        if(time == 0){
            parkourPvP.getGameManager().setInGameStatus(InGameStatus.PVP);

            /*try{
                //TODO: Start PvP Timer
                //TaskManager.JUMP_TASK_MANAGER.getCustomTaskManager().runTaskTimer(0L, 20L);
            }catch(TaskOverflowException e){
                e.printStackTrace();
            }*/

            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(uuid -> {
                final Player player = parkourPvP.getServer().getPlayer(uuid);
                new Title(player).sendActionBar("Que le meilleur gagne");
            });

            cancel();
            return;
        }

        time--;
    }

}
