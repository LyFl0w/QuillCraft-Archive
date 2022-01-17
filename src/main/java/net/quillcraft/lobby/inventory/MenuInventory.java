package net.quillcraft.lobby.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.builders.InventoryBuilder;
import net.quillcraft.core.utils.builders.ItemBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.lumy.api.text.Text;
import org.lumy.api.text.TextList;

public class MenuInventory {

    public final Inventory getMenuInventory(final Player player){
        return getMenuInventory(LanguageManager.getLanguage(player));
    }

    public final Inventory getMenuInventory(final Account account){
        return getMenuInventory(LanguageManager.getLanguage(account));
    }

    public final Inventory getMenuInventory(final LanguageManager language){
        final InventoryBuilder menuBuilder = new InventoryBuilder(9, language.getMessage(Text.INVENTORY_NAME_MENU));
        menuBuilder.setItem(4, new ItemBuilder(Material.IRON_BOOTS, ItemFlag.HIDE_ATTRIBUTES)
                .setName(language.getMessage(Text.ITEMS_INVENTORY_LOBBY_PARKOURPVP_NAME))
                .setLore(language.getMessage(TextList.ITEMS_INVENTORY_LOBBY_PARKOURPVP_LORE)).toItemStack());

        return menuBuilder.toInventory();
    }
}