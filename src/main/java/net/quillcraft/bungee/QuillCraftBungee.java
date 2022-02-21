package net.quillcraft.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.quillcraft.bungee.manager.DataManager;
import net.quillcraft.bungee.manager.PluginManager;
import net.quillcraft.bungee.subscriber.SubscriberManager;

public class QuillCraftBungee extends Plugin {

    private static QuillCraftBungee INSTANCE;

    @Override
    public void onEnable(){
        INSTANCE = this;

        DataManager.initAllData(this);
        SubscriberManager.initAllSubscribers();
        new PluginManager(this);
    }

    @Override
    public void onDisable(){
        SubscriberManager.removeAllSubscribersData();
        DataManager.closeAllData();
    }

    public static QuillCraftBungee getInstance(){
        return INSTANCE;
    }
}