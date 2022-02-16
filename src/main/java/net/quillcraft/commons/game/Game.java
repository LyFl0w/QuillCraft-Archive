package net.quillcraft.commons.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import org.redisson.api.RedissonClient;

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

    protected Game(){
        this.gameEnum = null;
        this.uuid = null;
        this.playerList = null;
        this.gameProperties = null;
    }

    public UUID getUUID(){
        return uuid;
    }

    public GameEnum getGameEnum(){
        return gameEnum;
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

    public int getMaxPlayer(){
        return gameProperties.getMaxPlayer();
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