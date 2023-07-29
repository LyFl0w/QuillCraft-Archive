package net.quillcraft.commons.game.waiter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.quillcraft.bungee.data.redis.RedisManager;
import net.quillcraft.commons.game.GameEnum;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WaitingList {

    @JsonIgnore
    protected final static RedissonClient redissonClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();

    private final List<Waiter> waiters;
    private final GameEnum gameEnum;

    public WaitingList(GameEnum gameEnum) {
        this.gameEnum = gameEnum;
        final RBucket<List<Waiter>> waitersListBucket = getWaitersListBucket();
        this.waiters = ((waitersListBucket.isExists()) ? waitersListBucket.get() : new ArrayList<>());
    }

    public List<Waiter> getWaitersList() {
        return waiters;
    }

    public void sortWaitersList() {
        waiters.sort(Comparator.comparingInt(Waiter::getPower));
    }

    public void updateWaitersListRedis() {
        final RBucket<List<Waiter>> listRBucket = getWaitersListBucket();
        if(waiters.size() == 0 && listRBucket.isExists()) {
            listRBucket.delete();
            return;
        }
        listRBucket.set(waiters);
    }

    private RBucket<List<Waiter>> getWaitersListBucket() {
        return redissonClient.getBucket(gameEnum.name()+".WAITINGLIST");
    }

}
