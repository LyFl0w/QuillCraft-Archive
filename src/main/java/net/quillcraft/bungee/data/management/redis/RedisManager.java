package net.quillcraft.bungee.data.management.redis;

import net.md_5.bungee.config.Configuration;
import net.quillcraft.bungee.manager.ConfigurationManager;

public enum RedisManager {

    TEXT(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.text.host"),
            getDataConfig().getString("redis.text.user.name"), getDataConfig().getString("redis.text.user.password"),
            getDataConfig().getInt("redis.text.database_number"), getDataConfig().getInt("redis.text.port")))),

    ACCOUNT(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.account.host"),
            getDataConfig().getString("redis.account.user.name"), getDataConfig().getString("redis.account.user.password"),
            getDataConfig().getInt("redis.account.database_number"), getDataConfig().getInt("redis.account.port")))),

    PARTY(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.party.host"),
            getDataConfig().getString("redis.party.user.name"), getDataConfig().getString("redis.party.user.password"),
            getDataConfig().getInt("redis.party.database_number"), getDataConfig().getInt("redis.party.port")))),

    MESSAGE(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.message.host"),
            getDataConfig().getString("redis.message.user.name"), getDataConfig().getString("redis.message.user.password"),
            getDataConfig().getInt("redis.message.database_number"), getDataConfig().getInt("redis.message.port")))),

    FRIEND(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.friend.host"),
            getDataConfig().getString("redis.friend.user.name"), getDataConfig().getString("redis.friend.user.password"),
            getDataConfig().getInt("redis.friend.database_number"), getDataConfig().getInt("redis.friend.port")))),

    RANK(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.rank.host"),
            getDataConfig().getString("redis.rank.user.name"), getDataConfig().getString("redis.rank.user.password"),
            getDataConfig().getInt("redis.rank.database_number"), getDataConfig().getInt("redis.rank.port"))));

    private final RedisAccess redisAccess;
    RedisManager(RedisAccess redisAccess){
        this.redisAccess = redisAccess;
    }

    public final RedisAccess getRedisAccess(){
        return redisAccess;
    }

    public static void initAllRedisAccess(){
        for(RedisManager redisManager : values()){
            redisManager.getRedisAccess().init();
        }
    }

    public static void closeAllRedisAccess(){
        for(RedisManager redisManager : values()){
            redisManager.getRedisAccess().close();
        }
    }

    private static Configuration getDataConfig(){
        return ConfigurationManager.DATA_ACCESS.getConfiguration();
    }

}
