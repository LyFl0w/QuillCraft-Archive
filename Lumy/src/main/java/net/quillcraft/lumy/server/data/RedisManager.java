package net.quillcraft.lumy.server.data;

public enum RedisManager{

    TEXT(new RedisAccess(RedisCredential.getRedisCrendential()));

    private final RedisAccess redisAccess;
    RedisManager(RedisAccess redisAccess){
        this.redisAccess = redisAccess;
    }

    public final RedisAccess getRedisAccess(){
        return redisAccess;
    }

}
