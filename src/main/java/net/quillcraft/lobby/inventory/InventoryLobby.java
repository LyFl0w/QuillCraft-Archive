package net.quillcraft.lobby.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.builders.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import net.quillcraft.lumy.api.text.Text;

public class InventoryLobby {

    private InventoryLobby() {
        throw new IllegalStateException("Utility class");
    }

    public static void setDefaultInventory(final Player player, final LanguageManager languageManager, final Account.Visibility visibility) {
        final PlayerInventory playerInventory = player.getInventory();

        playerInventory.setItem(0, new ItemBuilder(Material.FEATHER).setName(languageManager.getMessage(Text.ITEMS_INVENTORY_LOBBY_MENU_NAME)).toItemStack());
        playerInventory.setItem(1, new ItemBuilder(Material.GOLD_INGOT).setName(languageManager.getMessage(Text.ITEMS_INVENTORY_LOBBY_SHOP_NAME)).toItemStack());
        playerInventory.setItem(2, new ItemBuilder(Material.PLAYER_HEAD).setName(languageManager.getMessage(Text.ITEMS_INVENTORY_LOBBY_ACCOUNT_NAME)).setSkullOwner(player).toItemStack());
        playerInventory.setItem(3, new ItemBuilder(Material.PUFFERFISH).setName(languageManager.getMessage(Text.ITEMS_INVENTORY_LOBBY_FRIENDS_NAME)).toItemStack());
        playerInventory.setItem(4, new ItemBuilder(Material.EXPERIENCE_BOTTLE).setName(languageManager.getMessage(Text.ITEMS_INVENTORY_LOBBY_PARTICLES_NAME)).toItemStack());
        playerInventory.setItem(7, VisibilityInventory.getItemVisibility(visibility, languageManager).toItemStack());
        playerInventory.setItem(8, new ItemBuilder(Material.COMPARATOR).setName(languageManager.getMessage(Text.ITEMS_INVENTORY_LOBBY_PARAMETERS_NAME)).toItemStack());
    }

    public static void updateVisibility(final PlayerInventory playerInventory, final Account.Visibility visibility, final LanguageManager languageManager) {
        playerInventory.setItem(7, VisibilityInventory.getItemVisibility(visibility, languageManager).toItemStack());
    }

}
