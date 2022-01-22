package net.quillcraft.commons.game;

import net.quillcraft.core.data.management.redis.RedisManager;
import org.redisson.api.RBucket;

import java.util.ArrayList;
import java.util.List;

public abstract class Game{

    private final int id;
    private final List<String> playerList;
    private final GameProperties gameProperties;
    private GameStatus gameStatus;


    public Game(GameProperties gameProperties){
        this.gameStatus = GameStatus.STARTING;
        this.gameProperties = gameProperties;
        this.id = 0;
        this.playerList = new ArrayList<>();
    }

    public int getId(){
        return id;
    }

    public String getRedisKey(){
        return getClass().getName()+":"+id;
    }

    public List<String> getPlayerList(){
        return playerList;
    }

    public boolean isFullyFilled(){
        return gameProperties.getMaxPlayer() == playerList.size();
    }

    public void updateRedis(){
        final RBucket<Game> gameRBucket = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient().getBucket(getRedisKey());
        gameRBucket.set(this);
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