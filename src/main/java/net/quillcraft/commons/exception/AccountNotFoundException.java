package net.quillcraft.commons.exception;

import org.bukkit.entity.Player;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(Player player) {
        super("The account ("+player.getUniqueId()+") was not found");
        player.sendMessage("§cError: Account Not Found Exception");
    }

}
