package net.quillcraft.core.data.management.redis;

import net.quillcraft.core.QuillCraftCore;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.util.logging.Logger;

public class RedisAccess {

    private RedissonClient redissonClient;
    private final RedisCredential redisCredential;

    private final Logger logger = QuillCraftCore.getInstance().getLogger();

    public RedisAccess(RedisCredential redisCredential){
        this.redisCredential = redisCredential;
    }

    public final RedissonClient initRedisson(RedisCredential redisCredential){
        final Config config = new Config();

        config.setCodec(new JsonJacksonCodec());
        config.setThreads(2);
        config.setNettyThreads(4);
        config.useSingleServer()
                .setAddress(redisCredential.getAdress())
                .setPassword(redisCredential.getPassword())
                .setDatabase(redisCredential.getDatabase())
                .setClientName(redisCredential.getClientName());

        return Redisson.create(config);
    }

    public void init(){
        redissonClient = initRedisson(redisCredential);
    }

    public void close(){
        logger.info("ShutDown - Start");
        redissonClient.shutdown();
        logger.info("ShutDown - End");
    }

    public final RedissonClient getRedissonClient(){
        return redissonClient;
    }
}
