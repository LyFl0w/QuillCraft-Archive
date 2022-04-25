package net.quillcraft.lobby.utils;

import net.quillcraft.commons.account.Account;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.lobby.inventory.InventoryLobby;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PlayerUtils extends net.quillcraft.core.utils.PlayerUtils {

    public static void setLobbyParameters(final Player player, final Account account){
        player.getInventory().setHeldItemSlot(4);
        resetDefaultParmetersPlayer(player, GameMode.SURVIVAL);
        InventoryLobby.setDefaultInventory(player, LanguageManager.getLanguageByISO(account.getLanguageISO()), account.getVisibility());
    }

}