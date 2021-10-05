package net.quillcraft.lobby.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.lobby.manager.LanguageManager;
import net.quillcraft.lobby.text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class InventoryLobby {

    public static void setDefaultInventory(final Player player, final LanguageManager languageManager, final Account.Visibility visibility){
        final PlayerInventory playerInventory = player.getInventory();

        playerInventory.setItem(0, new ItemBuilder(Material.FEATHER).setName(languageManager.getMessage(Text.MENU_ITEM)).toItemStack());
        playerInventory.setItem(1, new ItemBuilder(Material.GOLD_INGOT).setName(languageManager.getMessage(Text.SHOP_ITEM)).toItemStack());
        playerInventory.setItem(2, new ItemBuilder(Material.PLAYER_HEAD).setName(languageManager.getMessage(Text.ACCOUNT_ITEM)).
                setSkullOwner(player).toItemStack());
        playerInventory.setItem(3, new ItemBuilder(Material.PUFFERFISH).setName(languageManager.getMessage(Text.FRIENDS_ITEM)).toItemStack());
        playerInventory.setItem(4, new ItemBuilder(Material.EXPERIENCE_BOTTLE).setName(languageManager.getMessage(Text.PARTICLES_ITEM)).toItemStack());
        playerInventory.setItem(7, VisibilityInventory.getItemVisibility(visibility, languageManager).toItemStack());
        playerInventory.setItem(8, new ItemBuilder(Material.COMPARATOR).setName(languageManager.getMessage(Text.PARAMETERS_ITEM)).toItemStack());
    }

    public static void updateVisibility(final PlayerInventory playerInventory, final Account.Visibility visibility, final LanguageManager languageManager){
        playerInventory.setItem(7, VisibilityInventory.getItemVisibility(visibility, languageManager).toItemStack());
    }

}
