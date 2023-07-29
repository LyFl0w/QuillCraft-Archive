package net.quillcraft.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.quillcraft.bungee.listeners.proxy.ProxyPingListener;
import net.quillcraft.bungee.manager.DataManager;
import net.quillcraft.bungee.manager.PluginManager;
import net.quillcraft.bungee.subscriber.SubscriberManager;

public class QuillCraftBungee extends Plugin {

    private static QuillCraftBungee INSTANCE;

    private DataManager dataManager;

    @Override
    public void onEnable(){
        INSTANCE = this;

        dataManager = new DataManager(this);
        dataManager.init();

        SubscriberManager.initAllSubscribers();

        ProxyPingListener.updateProtocolVersion(getProxy().getProtocolVersion());

        new PluginManager(this);
    }

    @Override
    public void onDisable(){
        SubscriberManager.removeAllSubscribersData();
        dataManager.close();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public static QuillCraftBungee getInstance(){
        return INSTANCE;
    }
}