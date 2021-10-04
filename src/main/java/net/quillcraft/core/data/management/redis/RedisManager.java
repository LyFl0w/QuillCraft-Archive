package net.quillcraft.core.data.management.redis;

import net.quillcraft.core.manager.ConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public enum RedisManager {

    TEXT(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.text.host"),
            getDataConfig().getString("redis.text.user.name"), getDataConfig().getString("redis.text.user.password"),
            getDataConfig().getInt("redis.text.database_number"), getDataConfig().getInt("redis.text.port")))),

    PLAYER_DATA(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.playerdata.host"),
            getDataConfig().getString("redis.playerdata.user.name"), getDataConfig().getString("redis.playerdata.user.password"),
            getDataConfig().getInt("redis.playerdata.database_number"), getDataConfig().getInt("redis.playerdata.port")))),

    PARTY_DATA(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.partydata.host"),
            getDataConfig().getString("redis.partydata.user.name"), getDataConfig().getString("redis.partydata.user.password"),
            getDataConfig().getInt("redis.partydata.database_number"), getDataConfig().getInt("redis.partydata.port")))),

    MESSAGE_DATA(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.message.host"),
    getDataConfig().getString("redis.message.user.name"), getDataConfig().getString("redis.message.user.password"),
    getDataConfig().getInt("redis.message.database_number"), getDataConfig().getInt("redis.message.port")))),

    RANK_DATA(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.rankdata.host"),
    getDataConfig().getString("redis.rankdata.user.name"), getDataConfig().getString("redis.rankdata.user.password"),
    getDataConfig().getInt("redis.rankdata.database_number"), getDataConfig().getInt("redis.rankdata.port"))));


    private final RedisAccess redisAccess;
    RedisManager(RedisAccess redisAccess){
        this.redisAccess = redisAccess;
    }

    public final RedisAccess getRedisAccess(){
        return redisAccess;
    }

    public static void initAllRedisAccess(){
        Arrays.stream(values()).forEach(redisManager -> redisManager.getRedisAccess().init());
    }

    public static void closeAllRedisAccess(){
        Arrays.stream(values()).forEach(redisManager -> redisManager.getRedisAccess().close());
    }

    private static FileConfiguration getDataConfig(){
        return ConfigurationManager.DATA_ACCESS.getConfiguration();
    }

}
