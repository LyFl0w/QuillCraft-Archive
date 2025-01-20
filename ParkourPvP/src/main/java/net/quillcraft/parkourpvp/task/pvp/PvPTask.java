package net.quillcraft.parkourpvp.task.pvp;

import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.scoreboard.PvPScoreboard;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class PvPTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;
    private final int timeToReach;
    private final int worldBorderTime;

    public PvPTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.parkourPvP = ((PvPTaskManager)customTaskManager).getJavaPlugin();
        this.time = 0;
        this.timeToReach = 300;
        this.worldBorderTime = 180;
    }

    @Override
    public void run(){

        new PvPScoreboard(parkourPvP).updateTime();

        // WORLDBORDER REDUCE
        if(time == worldBorderTime){
            parkourPvP.getGameManager().getWorlds()[1].getWorldBorder().setSize(0.0d, timeToReach-worldBorderTime);
            parkourPvP.getServer().broadcastMessage("La bordure se rétrécit !");
        }

        if(time == timeToReach){
            final Supplier<Stream<? extends Player>> players = () -> parkourPvP.getServer().getOnlinePlayers().stream().filter(player -> player.getGameMode() == GameMode.SURVIVAL);
            if(players.get().count() > 1){
                players.get().forEach(player -> player.damage(0.5d));
                return;
            }
            cancel();
            return;
        }

        time++;
    }

    public int getTime(){
        return time;
    }

    public int getTimeToReach(){
        return timeToReach;
    }
}
