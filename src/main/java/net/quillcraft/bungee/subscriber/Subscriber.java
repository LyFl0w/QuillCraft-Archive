package net.quillcraft.bungee.subscriber;

import net.md_5.bungee.api.ProxyServer;
import net.quillcraft.bungee.data.redis.RedisManager;
import org.redisson.api.RedissonClient;

public abstract class Subscriber {

    protected final static RedissonClient redissonClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();
    protected final ProxyServer proxyServer;

    public Subscriber(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    public abstract void read();

}
