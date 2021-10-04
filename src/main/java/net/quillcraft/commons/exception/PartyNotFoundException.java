package net.quillcraft.commons.exception;

import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.text.Text;
import org.bukkit.entity.Player;

public class PartyNotFoundException extends Exception{

    public PartyNotFoundException(Player player){
        player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.PARTY_NO_PARTY));
    }

    @Override
    public void printStackTrace(){
        return;
    }
}