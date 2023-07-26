package net.quillcraft.commons.exception;

import net.quillcraft.core.manager.LanguageManager;
import org.bukkit.entity.Player;
import org.lumy.api.text.Text;

public class PartyNotFoundException extends Exception{

    public PartyNotFoundException(Player player){
        player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.PARTY_NO_PARTY));
    }

    @Override
    public void printStackTrace(){ }
}