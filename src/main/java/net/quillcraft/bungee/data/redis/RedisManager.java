package net.quillcraft.bungee.data.redis;

import net.md_5.bungee.config.Configuration;
import net.quillcraft.bungee.manager.configuration.ConfigurationManager;

import java.util.Arrays;

public enum RedisManager {

    TEXT(getRedisAccess("text")),
    ACCOUNT(getRedisAccess("account")),
    PARTY(getRedisAccess("party")),
    FRIEND(getRedisAccess("friend")),
    MESSAGE(getRedisAccess("message")),
    RANK(getRedisAccess("rank")),
    GAME_SERVER(getRedisAccess("gameserver")),
    WEB_API(getRedisAccess("web-api")),
    STATISTIQUES(getRedisAccess("statistiques"));

    private final RedisAccess redisAccess;

    RedisManager(RedisAccess redisAccess) {
        this.redisAccess = redisAccess;
    }

    public static void initAllRedisAccess() {
        Arrays.stream(values()).forEach(redisManager -> redisManager.getRedisAccess().init());
    }

    public static void closeAllRedisAccess() {
        Arrays.stream(values()).forEach(redisManager -> redisManager.getRedisAccess().close());
    }

    private static Configuration getDataConfig() {
        return ConfigurationManager.DATA_ACCESS.getConfiguration();
    }

    public final RedisAccess getRedisAccess() {
        return redisAccess;
    }

    private static RedisAccess getRedisAccess(String name) {
        final String path = "redis."+name+".";
        return new RedisAccess(new RedisCredential(
                getDataConfig().getString(path+"host"),
                getDataConfig().getString(path+"user.name"),
                getDataConfig().getString(path+"user.password"),
                getDataConfig().getInt(path+"database_number"),
                getDataConfig().getInt(path+"port")));
    }

}
