package org.lumy.server.data;

import org.lumy.Lumy;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisAccess {

    private RedissonClient redissonClient;
    private final RedisCredential redisCredential;

    public RedisAccess(RedisCredential redisCredential){
        this.redisCredential = redisCredential;
    }

    private RedissonClient initRedisson(RedisCredential redisCredential){
        final Config config = new Config();

        config.setCodec(new JsonJacksonCodec());
        config.setThreads(2);
        config.setNettyThreads(4);
        config.useSingleServer()
                .setAddress(redisCredential.getAdress())
                .setPassword(redisCredential.password())
                .setDatabase(redisCredential.database())
                .setClientName(redisCredential.clientName());

        return Redisson.create(config);
    }

    public void init(){
        Lumy.logger.info("Redis init");
        redissonClient = initRedisson(redisCredential);
    }

    public void close(){
        if(redissonClient == null) return;
        Lumy.logger.info("ShutDown - Start");
        redissonClient.shutdown();
        Lumy.logger.info("ShutDown - End");
    }

    public final RedissonClient getRedissonClient(){
        return redissonClient;
    }

}
