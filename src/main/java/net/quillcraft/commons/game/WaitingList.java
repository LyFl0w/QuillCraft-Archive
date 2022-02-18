package net.quillcraft.commons.game;

import org.redisson.api.RBucket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        final RBucket<List<Waiter>> listRBucket = getWaitersListBucket();
        if(waiters.size() == 0 && listRBucket.isExists()){
            listRBucket.delete();
            return;
        }
        listRBucket.set(waiters);
    }

    private RBucket<List<Waiter>> getWaitersListBucket(){
        return Game.redissonClient.getBucket(gameEnum.name()+".WAITINGLIST");
    }

}
