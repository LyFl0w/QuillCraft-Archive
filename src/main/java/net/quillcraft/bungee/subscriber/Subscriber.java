package net.quillcraft.bungee.subscriber;

import net.quillcraft.bungee.data.management.redis.RedisManager;
import org.redisson.api.RedissonClient;

public abstract class Subscriber{

    protected final static RedissonClient redissonClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();

    public abstract void read();

}
