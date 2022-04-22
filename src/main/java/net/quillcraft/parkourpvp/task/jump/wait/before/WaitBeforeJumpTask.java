package net.quillcraft.parkourpvp.task.jump.wait.before;

import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.core.utils.Title;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.manager.TaskManager;

import net.quillcraft.parkourpvp.status.InGameStatus;
import net.quillcraft.parkourpvp.task.jump.JumpTaskManager;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class WaitBeforeJumpTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;

    public WaitBeforeJumpTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.parkourPvP = ((WaitBeforeJumpTaskManager)customTaskManager).getJavaPlugin();
        this.time = 15;
    }

    @Override
    public void run(){

        if(time % 5 == 0 && time >= 5){
            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(uuid -> {
                final Player player = parkourPvP.getServer().getPlayer(uuid);
                if(time != 15) player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1.0f, 0.1f);
                new Title(player).sendFullTitle(1, 1, 1, "Le jump commence", "dans "+time+" secondes");
            });
        }

        if(time < 5 && time > 0){
            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(uuid -> {
                final Player player = parkourPvP.getServer().getPlayer(uuid);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.AMBIENT, 0.5f, 2.0f);
                new Title(player).sendActionBar("Le jump commence dans "+time+"'s");
            });
        }

        if(time == 0){
            parkourPvP.getGameManager().setInGameStatus(InGameStatus.JUMP);

            try{
                //Start Jump timer
                ((JumpTaskManager) TaskManager.JUMP_TASK_MANAGER.getCustomTaskManager()).setStartedAtTimeMillis().runTaskTimer(0L, 20L);
            }catch(TaskOverflowException e){
                e.printStackTrace();
            }

            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(uuid -> {
                final Player player = parkourPvP.getServer().getPlayer(uuid);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1.0f, 1.0f);
                new Title(player).sendActionBar("C'est parti !");
            });

            cancel();
            return;
        }

        time--;
    }

}
