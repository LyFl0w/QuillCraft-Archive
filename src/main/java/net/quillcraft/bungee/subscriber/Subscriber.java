package net.quillcraft.bungee.subscriber;

import net.md_5.bungee.api.ProxyServer;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import org.redisson.api.RedissonClient;

public abstract class Subscriber{

    protected final ProxyServer proxyServer;
    public Subscriber(ProxyServer proxyServer){
        this.proxyServer = proxyServer;
    }

    protected final static RedissonClient redissonClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();

    public abstract void read();

}
