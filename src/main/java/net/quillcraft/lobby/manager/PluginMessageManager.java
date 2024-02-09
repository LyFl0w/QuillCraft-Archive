package net.quillcraft.lobby.manager;

import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.message.Message;
import net.quillcraft.lobby.message.MessageFriend;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
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
    public void onPluginMessageReceived(@NotNull String tag, @NotNull Player player, byte @NotNull [] bytes) {
        try {
            relayMessageData(tag, bytes);
        } catch(Exception exception) {
            quillCraftLobby.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private void relayMessageData(String channel, byte[] bytes) {
        if(!channel.startsWith("quillcraft:")) return;
        for(Channels channels : Channels.values()) {
            if(channels.getChannel().equals(channel)) {
                try {
                    channels.getaClass().getConstructor(QuillCraftLobby.class, byte[].class).newInstance(quillCraftLobby, bytes);
                } catch (Exception e) {
                    throw new IllegalCallerException("QuillCraftLobby constructor is not found !");
                }
                return;
            }
        }
        quillCraftLobby.getLogger().warning(() -> "§cRelay was not done properly ! (target_channel : "+channel+")");
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