package net.quillcraft.parkourpvp.task.end;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;

public class EndTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;

    public EndTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.parkourPvP = ((EndTaskManager)customTaskManager).getJavaPlugin();
        this.time = 15;
    }

    @Override
    public void run(){

        if(time == 10){
            // TODO : dire les stats

        }

        if(time == 0){
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("lobby_1");

            parkourPvP.getServer().getOnlinePlayers().forEach(player -> player.sendPluginMessage(parkourPvP, "Bungeecord", out.toByteArray()));

            cancel();
            return;
        }

        time--;
    }

}
