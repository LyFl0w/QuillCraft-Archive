package net.quillcraft.parkourpvp.task.wait;

import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.scoreboard.GameScoreboard;

public class LobbyTask extends CustomTask{

    // FIXME: 20/02/2022 REMOVE LOBBY FOR WAITING SALLE ( LOBBY IS JUST FOR THE BETA)

    private final ParkourPvP parkourPvP;
    private int time;
    private final int defaultTime;

    public LobbyTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.time = 60;
        this.defaultTime = time;
        this.parkourPvP = ((LobbyTaskManager)customTaskManager).getJavaPlugin();
    }

    @Override
    public void run(){

        if(time == 0){
            parkourPvP.getServer().broadcastMessage("§1Fin de l'attente : -> démarrage du jeu !");
            parkourPvP.getParkourPvPGame().setGameStatus(GeneralGameStatus.IN_GAME);
            parkourPvP.getParkourPvPGame().getPlayerUUIDList().stream().parallel()
                    .forEach(uuid -> new GameScoreboard(parkourPvP).setScoreboard(parkourPvP.getServer().getPlayer(uuid)));
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
