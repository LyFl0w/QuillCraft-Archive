package net.quillcraft.bungee.listeners.proxy;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.bungee.utils.MessageUtils;

import org.lumy.api.text.Text;
import org.lumy.api.text.TextList;

public class ProxyPingListener implements Listener {

    private final LanguageManager languageManger = LanguageManager.DEFAULT;


    @EventHandler
    public void onProxyPing(ProxyPingEvent event){
        final ServerPing serverPing = event.getResponse();

        serverPing.setDescriptionComponent(new TextComponent(MessageUtils.motd(languageManger.getMessage(TextList.SERVER_DESCRIPTION))));
        serverPing.setVersion(new ServerPing.Protocol(languageManger.getMessage(Text.SERVER_VERSION_DOESNT_MATCHED), 0));

        event.getResponse().getVersion().setProtocol(serverPing.getVersion().getProtocol());
    }

}