package net.quillcraft.bungee.subscriber;

import net.quillcraft.bungee.serialization.QuillCraftBungee;
import net.quillcraft.commons.game.GameEnum;
import net.quillcraft.commons.game.waiter.WaitingList;

import java.util.Arrays;

public enum SubscriberManager {

    SUBSCRIBER_GAME(new SubscriberGame(QuillCraftBungee.getInstance().getProxy()));

    private final Subscriber subscriber;

    SubscriberManager(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public static void removeAllSubscribersData() {
        Arrays.stream(GameEnum.values()).parallel().forEach(gameEnum -> {
            final WaitingList waitingList = new WaitingList(gameEnum);
            waitingList.getWaitersList().clear();
            waitingList.updateWaitersListRedis();
        });
    }

    public static void initAllSubscribers() {
        Arrays.stream(values()).parallel().forEach(subscriberManager -> subscriberManager.getSubscriber().read());
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

}
