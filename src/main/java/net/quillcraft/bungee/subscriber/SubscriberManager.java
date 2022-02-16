package net.quillcraft.bungee.subscriber;

import java.util.Arrays;

public enum SubscriberManager{

    SUBSCRIBER_GAME(new SubscriberGame());

    private final Subscriber subscriber;
    SubscriberManager(Subscriber subscriber){
        this.subscriber = subscriber;
    }

    public Subscriber getSubscriber(){
        return subscriber;
    }

    public static void initAllSubscribers(){
        Arrays.stream(values()).parallel().forEach(subscriberManager -> subscriberManager.getSubscriber().read());
    }

}
