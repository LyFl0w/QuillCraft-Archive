package net.quillcraft.bungee.data.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisAccess {

    private final RedisCredential redisCredential;
    private RedissonClient redissonClient;

    public RedisAccess(RedisCredential redisCredential) {
        this.redisCredential = redisCredential;
    }

    public final RedissonClient initRedisson(RedisCredential redisCredential) {
        final Config config = new Config();

        config.setCodec(new JsonJacksonCodec());
        config.setThreads(4);
        config.setNettyThreads(4);
        config.useSingleServer().setAddress(redisCredential.getAdress()).setPassword(redisCredential.password()).setDatabase(redisCredential.database()).setClientName(redisCredential.clientName());

        return Redisson.create(config);
    }

    public void init() {
        redissonClient = initRedisson(redisCredential);
    }

    public void close() {
        redissonClient.shutdown();
    }

    public final RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
