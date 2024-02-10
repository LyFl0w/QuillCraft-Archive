package net.quillcraft.bungee.manager;

import net.quillcraft.bungee.serialization.QuillCraftBungee;
import net.quillcraft.bungee.listeners.player.ChatListener;
import net.quillcraft.bungee.listeners.player.DisconnectListener;
import net.quillcraft.bungee.listeners.player.PostLoginListener;
import net.quillcraft.bungee.listeners.proxy.ProxyPingListener;
import net.quillcraft.bungee.utils.MessagesPropertiesUtils;

public class PluginManager {

    private final QuillCraftBungee quillCraftBungee;

    public PluginManager(QuillCraftBungee main) {
        this.quillCraftBungee = main;
        new MessagesPropertiesUtils().generateNewBundleMessagesProperties(true);

        registerListeners(main.getProxy().getPluginManager());
        registerCommand();
    }

    private void registerListeners(net.md_5.bungee.api.plugin.PluginManager pluginManager) {
        pluginManager.registerListener(quillCraftBungee, new PostLoginListener(quillCraftBungee));
        pluginManager.registerListener(quillCraftBungee, new ChatListener());
        pluginManager.registerListener(quillCraftBungee, new DisconnectListener(quillCraftBungee));

        pluginManager.registerListener(quillCraftBungee, new ProxyPingListener());

        //Register Message
        pluginManager.registerListener(quillCraftBungee, new PluginMessageManager(quillCraftBungee));
    }

    private void registerCommand() {
        //
    }

}
