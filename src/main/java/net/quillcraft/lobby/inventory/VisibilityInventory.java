package net.quillcraft.lobby.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.builders.InventoryBuilder;
import net.quillcraft.core.utils.builders.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import net.quillcraft.lumy.api.text.Text;

import java.util.Arrays;

public class VisibilityInventory {

    public static ItemBuilder getItemVisibility(final Account.Visibility visibility, final LanguageManager languageManager) {
        return switch(visibility) {
            case EVERYONE ->
                    setVisibilityItemBuilderName(languageManager, Text.STATUS_VISIBILITY_EVERYONE, Account.Visibility.EVERYONE);
            // TODO : CHANGE TEXT ITEM NAME FOR PARTY CASE
            case PARTY ->
                    setVisibilityItemBuilderName(languageManager, Text.STATUS_VISIBILITY_EVERYONE, Account.Visibility.PARTY);
            case FRIENDS ->
                    setVisibilityItemBuilderName(languageManager, Text.STATUS_VISIBILITY_FRIENDS, Account.Visibility.FRIENDS);
            case NOBODY ->
                    setVisibilityItemBuilderName(languageManager, Text.STATUS_VISIBILITY_NOBODY, Account.Visibility.NOBODY);
        };
    }

    private static ItemBuilder setVisibilityItemBuilderName(final LanguageManager languageManager, final Text text, Account.Visibility visibility) {
        return new ItemBuilder(visibility.getMaterial()).setName(languageManager.getMessage(Text.ITEMS_INVENTORY_LOBBY_VISIBILITY_NAME)
                .replace("%STATUS%", languageManager.getMessage(text)));
    }

    public final Inventory getVisibilityInventory(final Account account) {
        final LanguageManager language = LanguageManager.getLanguage(account);
        final InventoryBuilder menuBuilder = new InventoryBuilder(9, language.getMessage(Text.INVENTORY_NAME_VISIBILITY));

        final Account.Visibility visibility = account.getVisibility();

        Arrays.stream(Account.Visibility.values()).parallel().forEach(visibilitys -> {
            final ItemBuilder itemBuilder = getItemVisibility(visibilitys, language);
            if(visibility.equals(visibilitys)) {
                itemBuilder.addEnchant(Enchantment.DURABILITY, 1);
                itemBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            menuBuilder.setItem(visibilitys.getSlot(), itemBuilder.toItemStack());
        });

        return menuBuilder.toInventory();
    }
}