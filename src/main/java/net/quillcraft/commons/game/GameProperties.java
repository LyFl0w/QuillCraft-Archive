package net.quillcraft.commons.game;

import org.bukkit.configuration.file.FileConfiguration;

public class GameProperties{

    private int maxPlayer, minPlayer;

    public GameProperties(FileConfiguration fileConfiguration){
        this.maxPlayer = fileConfiguration.getInt("player.max");
        this.minPlayer = fileConfiguration.getInt("player.min");
    }

    public int getMaxPlayer(){
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer){
        this.maxPlayer = maxPlayer;
    }

    public int getMinPlayer(){
        return minPlayer;
    }

    public void setMinPlayer(int minPlayer){
        this.minPlayer = minPlayer;
    }

}