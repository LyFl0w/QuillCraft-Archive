package net.quillcraft.lobby.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.builders.InventoryBuilder;
import net.quillcraft.core.utils.builders.ItemBuilder;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.lumy.api.text.Text;

import java.util.Arrays;

public class VisibilityInventory {

    public final Inventory getVisibilityInventory(final Account account){
        final LanguageManager language = LanguageManager.getLanguage(account);
        final InventoryBuilder menuBuilder = new InventoryBuilder(9, language.getMessage(Text.INVENTORY_NAME_VISIBILITY));

        final Account.Visibility visibility = account.getVisibility();

        Arrays.stream(Account.Visibility.values()).parallel().forEach(visibilitys -> {
            ItemBuilder itemBuilder = getItemVisibility(visibilitys, language);
            if(visibility.equals(visibilitys)){
                itemBuilder.addEnchant(Enchantment.DURABILITY, 1);
                itemBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            menuBuilder.setItem(visibilitys.getSlot(), itemBuilder.toItemStack());
        });

        return menuBuilder.toInventory();
    }

    protected static ItemBuilder getItemVisibility(final Account.Visibility visibility, final LanguageManager languageManager){
        return switch(visibility){
            case EVERYONE -> setVisibilityItemBuilderName(languageManager, Text.STATUS_VISIBILITY_EVERYONE, new ItemBuilder(Material.LIME_DYE));
            case FRIENDS -> setVisibilityItemBuilderName(languageManager, Text.STATUS_VISIBILITY_FRIENDS, new ItemBuilder(Material.CYAN_DYE));
            case NOBODY -> setVisibilityItemBuilderName(languageManager, Text.STATUS_VISIBILITY_NOBODY, new ItemBuilder(Material.GRAY_DYE));
            case PARTY -> setVisibilityItemBuilderName(languageManager, Text.STATUS_VISIBILITY_EVERYONE, new ItemBuilder(Material.BLUE_DYE));
        };
    }

    private static ItemBuilder setVisibilityItemBuilderName(final LanguageManager languageManager, final Text text, final ItemBuilder visibilityItemBuilder){
        return visibilityItemBuilder.setName(languageManager.getMessage(Text.ITEMS_INVENTORY_LOBBY_VISIBILITY_NAME).replace("%STATUS%", languageManager.getMessage(text)));
    }
}