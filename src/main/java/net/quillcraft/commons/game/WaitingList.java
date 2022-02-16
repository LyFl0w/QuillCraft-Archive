package net.quillcraft.commons.game;

import org.redisson.api.RBucket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class WaitingList{

    private final List<Waiter> waiters;
    private final GameEnum gameEnum;

    public WaitingList(GameEnum gameEnum){
        this.gameEnum = gameEnum;
        final RBucket<List<Waiter>> waitersListBucket = getWaitersListBucket();
        this.waiters = ((waitersListBucket.isExists()) ? waitersListBucket.get() : new ArrayList<>());
    }

    public List<Waiter> getWaitersList(){
        return waiters;
    }

    public void sortWaitersList(){
        waiters.sort(Comparator.comparingInt(Waiter::getPower));
    }

    public void updateWaitersListRedis(){
        getWaitersListBucket().set(waiters);
    }

    private RBucket<List<Waiter>> getWaitersListBucket(){
        return Game.redissonClient.getBucket(gameEnum.name()+":waitinglist");
    }

}
