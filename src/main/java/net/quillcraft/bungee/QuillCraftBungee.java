package net.quillcraft.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.quillcraft.bungee.listeners.proxy.ProxyPingListener;
import net.quillcraft.bungee.manager.DataManager;
import net.quillcraft.bungee.manager.PluginManager;
import net.quillcraft.bungee.subscriber.SubscriberManager;

public class QuillCraftBungee extends Plugin {

    private static QuillCraftBungee instance;

    private DataManager dataManager;

    @Override
    public void onEnable() {
        instance = this;

        dataManager = new DataManager(this);
        dataManager.init();

        SubscriberManager.initAllSubscribers();

        ProxyPingListener.updateProtocolVersion(getProxy().getProtocolVersion());

        new PluginManager(this);
    }

    @Override
    public void onDisable() {
        SubscriberManager.removeAllSubscribersData();
        dataManager.close();
    }

    public static QuillCraftBungee getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}