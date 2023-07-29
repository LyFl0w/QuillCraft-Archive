package net.quillcraft.bungee.manager;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.messages.*;

import java.util.Arrays;
import java.util.logging.Level;

public class PluginMessageManager implements Listener {

    private final ProxyServer proxy;

    public PluginMessageManager(QuillCraftBungee quillCraftBungee) {
        this.proxy = quillCraftBungee.getProxy();
        initMessageListener();
    }

    //Relay data
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        try {
            Channels.relayMessageData(event.getTag(), proxy, event);
        } catch(Exception exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    //Open Channel to communicate with Spigot Servers
    private void initMessageListener() {
        Arrays.stream(Channels.values()).parallel().forEach(channel -> proxy.registerChannel(channel.getChannel()));
    }

    private enum Channels {

        PARTY("quillcraft:party", MessageParty.class),
        MESSAGE("quillcraft:message", MessageMessage.class),
        FRIEND("quillcraft:friend", MessageFriend.class),
        GAME("quillcraft:game", MessageGame.class);

        private final String channel;
        private final Class<? extends Message> aClass;

        Channels(String channel, Class<? extends Message> aClass) {
            this.channel = channel;
            this.aClass = aClass;
        }

        public static void relayMessageData(String channel, ProxyServer proxy, PluginMessageEvent event) throws Exception {
            if(!channel.startsWith("quillcraft:")) return;
            for(Channels channels : values()) {
                if(channels.getChannel().equals(channel)) {
                    channels.getaClass().getConstructor(ProxyServer.class, PluginMessageEvent.class).newInstance(proxy, event);
                    return;
                }
            }
            proxy.getLogger().warning("§cRelay was not done properly ! (target_channel : "+channel+")");
        }

        public String getChannel() {
            return channel;
        }

        public Class<? extends Message> getaClass() {
            return aClass;
        }
    }

}