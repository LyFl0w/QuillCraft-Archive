package net.quillcraft.parkourpvp.task.lobby;

import net.quillcraft.core.task.CustomTaskManager;

import net.quillcraft.parkourpvp.ParkourPvP;

public class LobbyTaskManager extends CustomTaskManager{

    // FIXME: 20/02/2022 REMOVE LOBBY FOR WAITING SALLE ( LOBBY IS JUST FOR THE BETA)

    public LobbyTaskManager(ParkourPvP parkourPvP){
        super(parkourPvP, LobbyTask.class);
    }

    @Override
    public LobbyTask getTask(){
        return (LobbyTask) super.getTask();
    }

    @Override @SuppressWarnings("unchecked")
    protected ParkourPvP getJavaPlugin(){
        return (ParkourPvP) javaPlugin;
    }


}
