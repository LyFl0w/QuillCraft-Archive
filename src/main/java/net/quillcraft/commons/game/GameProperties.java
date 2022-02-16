package net.quillcraft.commons.game;


public class GameProperties{

    private int maxPlayer, minPlayer;

    private GameProperties(){}

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