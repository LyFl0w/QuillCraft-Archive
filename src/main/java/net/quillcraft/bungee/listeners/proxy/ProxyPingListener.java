package net.quillcraft.bungee.listeners.proxy;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.bungee.utils.MessageUtils;
import net.quillcraft.lumy.api.text.Text;
import net.quillcraft.lumy.api.text.TextList;

public class ProxyPingListener implements Listener {

    private static final LanguageManager languageManger = LanguageManager.DEFAULT;
    private static TextComponent description = new TextComponent(MessageUtils.motd(languageManger.getMessage(TextList.SERVER_DESCRIPTION)));
    private static ServerPing.Protocol protocolText = new ServerPing.Protocol(languageManger.getMessage(Text.SERVER_VERSION_DOESNT_MATCHED), 0);

    public static void updateProtocolVersion(int protocolInt) {
        protocolText.setProtocol(protocolInt);
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        final ServerPing serverPing = event.getResponse();

        serverPing.setDescriptionComponent(description);
        serverPing.setVersion(protocolText);
    }

    private static void updateServeurDescription(int protocolInt) {
        description = new TextComponent(MessageUtils.motd(languageManger.getMessage(TextList.SERVER_DESCRIPTION)));
        protocolText = new ServerPing.Protocol(languageManger.getMessage(Text.SERVER_VERSION_DOESNT_MATCHED), protocolInt);
    }

}