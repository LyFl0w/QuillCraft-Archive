package net.quillcraft.commons.exception;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.manager.LanguageManager;
import org.lumy.api.text.Text;

public class PartyNotFoundException extends RuntimeException {

    public PartyNotFoundException(ProxiedPlayer player){
        player.sendMessage(LanguageManager.getLanguage(player).getMessageComponent(Text.PARTY_NO_PARTY));
    }

}