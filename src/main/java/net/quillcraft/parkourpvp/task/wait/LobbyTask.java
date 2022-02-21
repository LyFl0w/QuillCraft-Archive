package net.quillcraft.parkourpvp.task.wait;

import net.quillcraft.core.task.CustomTask;
import net.quillcraft.parkourpvp.ParkourPvP;

public class LobbyTask extends CustomTask{

    // FIXME: 20/02/2022 REMOVE LOBBY FOR WAITING SALLE ( LOBBY IS JUST FOR THE BETA)

    private final ParkourPvP parkourPvP;
    private int time;
    private final int defaultTime;

    public LobbyTask(LobbyTaskManager lobbyTaskManager){
        super(lobbyTaskManager);
        this.time = 60;
        this.defaultTime = time;
        this.parkourPvP = lobbyTaskManager.getJavaPlugin();
    }

    @Override
    public void run(){

        if(time == 0){
            parkourPvP.getServer().broadcastMessage("§1Fin de l'attente : -> démarrage du jeu !");
            cancel();
            return;
        }

        if((time <= 5 && time > 0) || time == 10 || time == 15 || time == 30 || time == 60){
            parkourPvP.getServer().broadcastMessage("§6Le jeu démarre dans §b"+time+"§6's");
        }

        time--;
    }

    public int getTime(){
        return time;
    }

    public void setTime(int time){
        this.time = time;
    }

    public int getDefaultTime(){
        return defaultTime;
    }
}
