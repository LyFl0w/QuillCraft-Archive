package net.quillcraft.lobby.manager;

import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.message.Message;
import net.quillcraft.lobby.message.MessageFriend;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.logging.Level;

public class PluginMessageManager implements PluginMessageListener {

    private final QuillCraftLobby quillCraftLobby;

    public PluginMessageManager(QuillCraftLobby quillCraftLobby) {
        this.quillCraftLobby = quillCraftLobby;
        initMessageListener();
    }

    //Open Channel to communicate with Bungeecord Servers
    private void initMessageListener() {
        final Messenger messenger = quillCraftLobby.getServer().getMessenger();
        Arrays.stream(Channels.values()).forEach(channel ->
                messenger.registerIncomingPluginChannel(quillCraftLobby, channel.getChannel(), this));
    }

    // relay data
    @Override
    public void onPluginMessageReceived(@NotNull String tag, @NotNull Player player, @NotNull byte[] bytes) {
        try {
            relayMessageData(tag, bytes);
        } catch(Exception exception) {
            quillCraftLobby.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private void relayMessageData(String channel, byte[] bytes) throws Exception {
        if(!channel.startsWith("quillcraft:")) return;
        for(Channels channels : Channels.values()) {
            if(channels.getChannel().equals(channel)) {
                channels.getaClass().getConstructor(QuillCraftLobby.class, byte[].class).newInstance(quillCraftLobby, bytes);
                return;
            }
        }
        quillCraftLobby.getLogger().warning("§cRelay was not done properly ! (target_channel : "+channel+")");
    }


    private enum Channels {

        FRIEND("quillcraft:friend", MessageFriend.class),
        PARTY("quillcraft:party", MessageFriend.class);

        private final String channel;
        private final Class<? extends Message> aClass;

        Channels(String channel, Class<? extends Message> aClass) {
            this.channel = channel;
            this.aClass = aClass;
        }

        public String getChannel() {
            return channel;
        }

        public Class<? extends Message> getaClass() {
            return aClass;
        }

    }

}