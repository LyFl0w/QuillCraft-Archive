package net.quillcraft.bungee.manager;

import net.md_5.bungee.api.ProxyServer;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.listeners.player.ChatListener;
import net.quillcraft.bungee.listeners.player.DisconnectListener;
import net.quillcraft.bungee.listeners.player.PostLoginListener;
import net.quillcraft.bungee.listeners.proxy.ProxyPingListener;
import net.quillcraft.bungee.utils.MessagesPropertiesUtils;

public class PluginManager {

    private final QuillCraftBungee quillCraftBungee;
    private final ProxyServer proxy;
    private final net.md_5.bungee.api.plugin.PluginManager pluginManager;
    public PluginManager(QuillCraftBungee main){
        this.quillCraftBungee = main;
        this.proxy = main.getProxy();
        new MessagesPropertiesUtils().generateNewBundleMessagesProperties(true);

        this.pluginManager = proxy.getPluginManager();

        registerListeners();
        registerCommand();
    }

    private void registerListeners(){
        pluginManager.registerListener(quillCraftBungee, new PostLoginListener(quillCraftBungee));
        pluginManager.registerListener(quillCraftBungee, new ChatListener());
        pluginManager.registerListener(quillCraftBungee, new DisconnectListener());

        pluginManager.registerListener(quillCraftBungee, new ProxyPingListener());

        //Register Message
        pluginManager.registerListener(quillCraftBungee, new PluginMessageManager(quillCraftBungee));
    }

    private void registerCommand(){
        //
    }

}
