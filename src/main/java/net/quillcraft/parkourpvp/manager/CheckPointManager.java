package net.quillcraft.parkourpvp.manager;

import net.quillcraft.parkourpvp.game.CheckPoint;

import java.util.ArrayList;

public enum CheckPointManager{

    CHECKPOINTS(new ArrayList<>());

    private final ArrayList<CheckPoint> checkPoints;
    CheckPointManager(ArrayList<CheckPoint> checkPoints){
        this.checkPoints = checkPoints;
    }

    public ArrayList<CheckPoint> getCheckPoints(){
        return checkPoints;
    }
}
