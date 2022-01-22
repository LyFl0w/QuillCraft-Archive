package net.quillcraft.commons.game;

import net.quillcraft.core.data.management.redis.RedisManager;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Game{

    private final RedissonClient redissonClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();

    private final UUID uuid;
    private final List<String> playerList;

    private final GameProperties gameProperties;
    private GameStatus gameStatus;

    public Game(GameProperties gameProperties){
        this.gameStatus = GameStatus.STARTING;
        this.gameProperties = gameProperties;
        this.playerList = new ArrayList<>();
        this.uuid = UUID.randomUUID();
    }

    public UUID getUUID(){
        return uuid;
    }

    public String getName(){
        return getClass().getName();
    }

    public String getRedisKey(){
        return getName()+":"+uuid;
    }

    public List<String> getPlayerList(){
        return playerList;
    }

    public boolean isFullyFilled(){
        return gameProperties.getMaxPlayer() == playerList.size();
    }

    public void updateRedis(){
        final RBucket<Game> gameRBucket = redissonClient.getBucket(getRedisKey());
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