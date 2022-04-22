package net.quillcraft.parkourpvp.task.jump.wait.after;

import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.core.utils.MessageUtils;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.status.InGameStatus;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class WaitAfterJumpTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;

    public WaitAfterJumpTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.parkourPvP = ((WaitAfterJumpTaskManager)customTaskManager).getJavaPlugin();
        this.time = 15;
    }

    @Override
    public void run(){

        if(time == 10){
            final Server server = parkourPvP.getServer();

            final String line = MessageUtils.line();
            parkourPvP.getGameManager().getPlayersData().values().forEach(playerData -> {
                final Player player = server.getPlayer(playerData.getUuid());
                final StringBuilder jumpStatsDescribe = new StringBuilder(MessageUtils.chatCenteredMessage("§lStatistiques§f\n"));

                // Number of jumps
                jumpStatsDescribe.append(MessageUtils.chatCenteredMessage("Vous avez sauté "+playerData.getJump()+" fois\n"));

                // Number of times the player has fallen
                jumpStatsDescribe.append(MessageUtils.chatCenteredMessage("Vous êtes réapparus "+playerData.getRespawn()+" fois"));

                // Time to finish the parkour
                if(playerData.hasFinishParkour()) jumpStatsDescribe.append("\n")
                        .append(MessageUtils.chatCenteredMessage(playerData.getFormatedTimeToFinishParkour(new StringBuilder("Temps : ")).toString()));

                player.sendMessage(line+jumpStatsDescribe.append("\n")+line);
            });
        }

        if(time == 0){
            parkourPvP.getGameManager().setInGameStatus(InGameStatus.WAITING_BEFORE_PVP);

            try{
                //Start Jump timer
                TaskManager.WAIT_BEFORE_PVP_TASK_MANAGER.getCustomTaskManager().runTaskTimer(0L, 20L);
            }catch(TaskOverflowException e){
                e.printStackTrace();
            }

            cancel();
            return;
        }

        time--;
    }

}
