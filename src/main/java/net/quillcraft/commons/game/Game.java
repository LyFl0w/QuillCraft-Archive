package net.quillcraft.commons.game;

public abstract class Game{

    private final String name;
    private final int id;
    private GameStatus gameStatus;

    public Game(String name){
        this.name = name;
        this.id = 0;
        this.gameStatus = GameStatus.STARTING;
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }

    public String getRedisKey(){
        return name+":"+id;
    }

    public GameStatus getGameStatus(){
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus){
        this.gameStatus = gameStatus;
    }

    public boolean actualGameStatusIs(GameStatus gameStatus){
        return this.gameStatus == gameStatus;
    }
}