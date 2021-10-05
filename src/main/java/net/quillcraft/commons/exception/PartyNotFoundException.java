package net.quillcraft.commons.exception;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.bungee.text.Text;

public class PartyNotFoundException extends Exception{

    public PartyNotFoundException(ProxiedPlayer player){
        //super("The party of ("+player.getUniqueId().toString()+") was not found");
        player.sendMessage(new TextComponent(LanguageManager.getLanguage(player).getMessage(Text.PARTY_NO_PARTY)));
    }

    @Override
    public void printStackTrace(){
        return;
    }
}