package net.quillcraft.bungee.messages;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;

public abstract class Message {

    protected final ProxyServer proxy;

    public Message(ProxyServer proxyServer, PluginMessageEvent event) {
        this.proxy = proxyServer;
        onPluginMessagePlayer(event);
    }

    private void onPluginMessagePlayer(PluginMessageEvent event) {
        final byte[] data = event.getData();
        final ProxiedPlayer player = proxy.getPlayer(event.getReceiver().toString());

        final ByteArrayDataInput in = ByteStreams.newDataInput(data);
        final String sub = in.readUTF();

        onPluginMessageRepPlayer(player, sub, in);
    }

    protected abstract void onPluginMessageRepPlayer(ProxiedPlayer player, String sub, ByteArrayDataInput in);

}
