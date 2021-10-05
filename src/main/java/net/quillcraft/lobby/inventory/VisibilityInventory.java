package net.quillcraft.lobby.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.core.utils.builders.InventoryBuilder;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.lobby.manager.LanguageManager;
import net.quillcraft.lobby.text.Text;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;

public class VisibilityInventory {

    public final Inventory getVisibilityInventory(final Account account){
        final LanguageManager language = LanguageManager.getLanguage(account);
        final InventoryBuilder menuBuilder = new InventoryBuilder(9, language.getMessage(Text.VISIBILITY_INVENTORY));

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


    public static ItemBuilder getItemVisibility(final Account.Visibility visibility, final LanguageManager languageManager){
        return switch(visibility){
            case EVERYONE -> setVisibilityItemBuilderName(languageManager, Text.VISIBILITY_ITEM_STATUS_EVERYONE, new ItemBuilder(Material.LIME_DYE));
            case FRIENDS -> setVisibilityItemBuilderName(languageManager, Text.VISIBILITY_ITEM_STATUS_FRIENDS, new ItemBuilder(Material.CYAN_DYE));
            case NOBODY -> setVisibilityItemBuilderName(languageManager, Text.VISIBILITY_ITEM_STATUS_NOBODY, new ItemBuilder(Material.GRAY_DYE));
        };
    }

    private static ItemBuilder setVisibilityItemBuilderName(final LanguageManager languageManager, final Text text, final ItemBuilder visibilityItemBuilder){
        return visibilityItemBuilder.setName(languageManager.getMessage(Text.VISIBILITY_ITEM).replace("%STATUS%", languageManager.getMessage(text)));
    }
}