package net.quillcraft.parkourpvp.task.pvp;

import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.commons.game.statistiques.parkourpvp.PlayerParkourPvPData;
import net.quillcraft.parkourpvp.manager.GameManager;

import java.util.Collection;

public class PvPTaskManager extends CustomTaskManager{

    public PvPTaskManager(ParkourPvP parkourPvP){
        super(parkourPvP, PvPTask.class);
    }

    @Override
    public PvPTask getTask(){
        return (PvPTask) super.getTask();
    }

    @Override @SuppressWarnings("unchecked")
    protected ParkourPvP getJavaPlugin(){
        return (ParkourPvP) javaPlugin;
    }

    public void startDefaultTask(){
        final ParkourPvP parkourPvP = getJavaPlugin();
        final GameManager gameManager = parkourPvP.getGameManager();
        final Collection<PlayerParkourPvPData> playersData = gameManager.getPlayersDataGame().values();

        playersData.stream().filter(playerData -> !playerData.hasFinishParkour()).forEach(playerData ->
                playersData.forEach(otherPlayerData -> parkourPvP.getServer().getPlayer(playerData.getUuid())
                        .showPlayer(parkourPvP, parkourPvP.getServer().getPlayer(otherPlayerData.getUuid()))));

    }


}
