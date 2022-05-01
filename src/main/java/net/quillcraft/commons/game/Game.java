package net.quillcraft.commons.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.quillcraft.commons.game.properties.GameProperties;
import net.quillcraft.commons.game.status.GeneralGameStatus;
import net.quillcraft.core.data.management.redis.RedisManager;

import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract sealed class Game permits ParkourPvPGame{

    @JsonIgnore
    protected final static RedissonClient redissonClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();

    private final int id;
    private final List<UUID> playerList;
    private final GameProperties gameProperties;
    private final GameEnum gameEnum;

    private GeneralGameStatus generalGameStatus;

    public Game(GameEnum gameEnum, int id, GameProperties gameProperties){
        this.generalGameStatus = GeneralGameStatus.STARTING_SERVER;
        this.gameEnum = gameEnum;
        this.gameProperties = gameProperties;
        this.playerList = new ArrayList<>();
        this.id = id;

        updateRedis();
    }

    protected Game(){
        this.gameEnum = null;
        this.playerList = null;
        this.gameProperties = null;
        this.id = 0;
    }

    public int getId(){
        return id;
    }

    public String getRedisKey(){
        return gameEnum.name()+":"+id;
    }

    public List<UUID> getPlayerUUIDList(){
        return playerList;
    }

    public boolean isFullyFilled(){
        return gameProperties.getMaxPlayer() == playerList.size();
    }

    public GameProperties getGameProperties(){
        return gameProperties;
    }

    public abstract void updateRedis();

    public void deleteRedisKey(){
        redissonClient.getBucket(getRedisKey()).delete();
    }

    public void searchPlayer(){
        redissonClient.getTopic("game.searchplayer").publish(getRedisKey());
    }

    public GeneralGameStatus getGameStatus(){
        return generalGameStatus;
    }

    public void setGameStatus(GeneralGameStatus generalGameStatus){
        this.generalGameStatus = generalGameStatus;
    }

    public boolean actualGameStatusIs(GeneralGameStatus generalGameStatus){
        return this.generalGameStatus == generalGameStatus;
    }
}