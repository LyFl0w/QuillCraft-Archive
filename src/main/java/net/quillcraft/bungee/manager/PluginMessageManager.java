package net.quillcraft.bungee.manager;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.messages.MessageFriend;
import net.quillcraft.bungee.messages.MessageMessage;
import net.quillcraft.bungee.messages.MessageParty;

import java.util.Arrays;

public class PluginMessageManager implements Listener {

    private final ProxyServer proxy;

    public PluginMessageManager(QuillCraftBungee quillCraftBungee, String... channels){
        this.proxy = quillCraftBungee.getProxy();
        initMessageListener(channels);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event){
        switch(event.getTag()){
            case "quillcraft:party" -> new MessageParty(proxy, event);
            case "quillcraft:message" -> new MessageMessage(proxy, event);
            case "quillcraft:friend" -> new MessageFriend(proxy, event);
        }
    }

    private void initMessageListener(String... channels){
        Arrays.stream(channels).parallel().forEach(proxy::registerChannel);
    }

}