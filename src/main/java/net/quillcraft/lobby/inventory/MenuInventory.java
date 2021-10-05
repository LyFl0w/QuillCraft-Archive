package net.quillcraft.lobby.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.core.utils.builders.InventoryBuilder;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.lobby.manager.LanguageManager;
import net.quillcraft.lobby.text.Text;
import net.quillcraft.lobby.text.TextList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuInventory {

    public final Inventory getMenuInventory(final Player player){
        return getMenuInventory(LanguageManager.getLanguage(player));
    }

    public final Inventory getMenuInventory(final Account account){
        return getMenuInventory(LanguageManager.getLanguage(account));
    }

    public final Inventory getMenuInventory(final LanguageManager language){
        final InventoryBuilder menuBuilder = new InventoryBuilder(9, language.getMessage(Text.MENU_INVENTORY));
        menuBuilder.setItem(4, new ItemBuilder(Material.IRON_BOOTS, 1).setName(language.getMessage(Text.INVENTORY_MENU_PARKOURPVP_ITEM)).
                setLore(language.getMessage(TextList.LORE_PARKOURPVP_TEXT)).toItemStack());

        return menuBuilder.toInventory();
    }
}