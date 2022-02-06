package net.quillcraft.commons.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.quillcraft.core.data.management.redis.RedisManager;

import org.redisson.api.RBucket;;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract sealed class Game permits ParkourPvPGame{

    @JsonIgnore
    protected final static RedissonClient redissonClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();

    private final UUID uuid;
    private final List<UUID> playerList;

    private final GameProperties gameProperties;
    private GeneralGameStatus generalGameStatus;

    private final GameEnum gameEnum;

    public Game(GameEnum gameEnum, GameProperties gameProperties){
        this.generalGameStatus = GeneralGameStatus.STARTING_SERVER;
        this.gameEnum = gameEnum;
        this.gameProperties = gameProperties;
        this.playerList = new ArrayList<>();
        this.uuid = UUID.randomUUID();

        updateRedis();
    }

    protected Game(){
        this.gameEnum = null;
        this.uuid = null;
        this.playerList = null;
        this.gameProperties = null;
    }

    public UUID getUUID(){
        return uuid;
    }

    public String getRedisKey(){
        return gameEnum.name()+":"+uuid;
    }

    public List<UUID> getPlayerUUIDList(){
        return playerList;
    }

    public boolean isFullyFilled(){
        return gameProperties.getMaxPlayer() == playerList.size();
    }

    public abstract void updateRedis();

    private WaitingList waitingList(){
        final RBucket<WaitingList> waitingListRBucket = redissonClient.getBucket(gameEnum.name()+":waitinglist");
        return waitingListRBucket.get();
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