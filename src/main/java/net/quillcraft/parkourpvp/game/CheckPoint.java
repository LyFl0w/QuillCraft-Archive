package net.quillcraft.parkourpvp.game;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.UUID;

public class CheckPoint{

    public static int coins;

    private final int id;
    private final ArrayList<UUID> players;
    private final Location location;

    public CheckPoint(Location location, int id){
        this.location = location;
        this.players = new ArrayList<>();
        this.id = id;
    }

    public Location getLocation(){
        return location;
    }

    public ArrayList<UUID> getPlayers(){
        return players;
    }

    public int getId(){
        return id;
    }

    public String addPlayer(PlayerDataGame playerDataGame){
        final StringBuilder message = new StringBuilder();

        players.add(playerDataGame.getUuid());
        playerDataGame.addCoins(coins);

        message.append(coins).append(" coins");

        final int size = players.size();
        if(size <= 3){
            final int bonus = CheckPointCoinsBonus.getBonus(size);
            if(bonus != 0){
                playerDataGame.addCoins(bonus);
                message.append(" + ").append(bonus).append(" coins for bonus position");
            }
        }

        return message.toString();
    }

}
