package net.quillcraft.commons.game;

import org.redisson.api.RBucket;

public non-sealed class ParkourPvPGame extends Game{

    private ParkourPvPGame(){}

    public ParkourPvPGame(GameProperties gameProperties){
        super(GameEnum.PARKOUR_PVP_SOLO, gameProperties);
    }

    @Override
    public void updateRedis(){
        final RBucket<ParkourPvPGame> gameRBucket = redissonClient.getBucket(getRedisKey());
        gameRBucket.set(this);
    }


}