package net.quillcraft.bungee.listeners.proxy;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.quillcraft.bungee.manager.ConfigurationManager;
import net.quillcraft.bungee.text.Text;
import net.quillcraft.bungee.text.TextList;
import net.quillcraft.bungee.utils.MessageUtils;

public class ProxyPingListener implements Listener {

    private final static Configuration configuration = ConfigurationManager.DEFAULT_CONFIGURATION.getConfiguration();
    private final static TextComponent description = new TextComponent(MessageUtils.motd(configuration.getStringList(TextList.SERVER_DESCRIPTION.getPath())));
    private final static ServerPing.Protocol protocol = new ServerPing.Protocol(configuration.getString(Text.SERVER_VERSION_DOESNT_MATCHED.getPath()), 0);

    @EventHandler
    public void onProxyPing(ProxyPingEvent event){
        final ServerPing serverPing = event.getResponse();
        protocol.setProtocol(serverPing.getVersion().getProtocol());

        serverPing.setDescriptionComponent(description);
        serverPing.setVersion(protocol);
    }

}