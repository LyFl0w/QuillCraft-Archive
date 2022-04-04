package net.quillcraft.parkourpvp.game;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.UUID;

public class CheckPoint{

    private final ArrayList<UUID> players;
    private final Location location;
    public static int coins, bonusFirst, bonusSecond, bonusThird;

    public CheckPoint(Location location){
        this.location = location;
        this.players = new ArrayList<>();
    }

    public Location getLocation(){
        return location;
    }

    public ArrayList<UUID> getPlayers(){
        return players;
    }
}
