package net.quillcraft.core.data.management.redis;

import net.quillcraft.core.manager.ConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public enum RedisManager {

    TEXT(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.text.host"), getDataConfig().getString("redis.text.user.name"), getDataConfig().getString("redis.text.user.password"), getDataConfig().getInt("redis.text.database_number"), getDataConfig().getInt("redis.text.port")))),

    ACCOUNT(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.account.host"), getDataConfig().getString("redis.account.user.name"), getDataConfig().getString("redis.account.user.password"), getDataConfig().getInt("redis.account.database_number"), getDataConfig().getInt("redis.account.port")))),

    PARTY(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.party.host"), getDataConfig().getString("redis.party.user.name"), getDataConfig().getString("redis.party.user.password"), getDataConfig().getInt("redis.party.database_number"), getDataConfig().getInt("redis.party.port")))),

    FRIEND(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.friend.host"), getDataConfig().getString("redis.friend.user.name"), getDataConfig().getString("redis.friend.user.password"), getDataConfig().getInt("redis.friend.database_number"), getDataConfig().getInt("redis.friend.port")))),

    MESSAGE(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.message.host"), getDataConfig().getString("redis.message.user.name"), getDataConfig().getString("redis.message.user.password"), getDataConfig().getInt("redis.message.database_number"), getDataConfig().getInt("redis.message.port")))),

    RANK(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.rank.host"), getDataConfig().getString("redis.rank.user.name"), getDataConfig().getString("redis.rank.user.password"), getDataConfig().getInt("redis.rank.database_number"), getDataConfig().getInt("redis.rank.port")))),

    GAME_SERVER(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.gameserver.host"), getDataConfig().getString("redis.gameserver.user.name"), getDataConfig().getString("redis.gameserver.user.password"), getDataConfig().getInt("redis.gameserver.database_number"), getDataConfig().getInt("redis.gameserver.port")))),

    WEB_API(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.web-api.host"), getDataConfig().getString("redis.web-api.user.name"), getDataConfig().getString("redis.web-api.user.password"), getDataConfig().getInt("redis.web-api.database_number"), getDataConfig().getInt("redis.web-api.port")))),

    STATISTIQUES(new RedisAccess(new RedisCredential(getDataConfig().getString("redis.statistiques.host"), getDataConfig().getString("redis.statistiques.user.name"), getDataConfig().getString("redis.statistiques.user.password"), getDataConfig().getInt("redis.statistiques.database_number"), getDataConfig().getInt("redis.statistiques.port"))));


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

    private static FileConfiguration getDataConfig() {
        return ConfigurationManager.DATA_ACCESS.getConfiguration();
    }

    public final RedisAccess getRedisAccess() {
        return redisAccess;
    }

}
