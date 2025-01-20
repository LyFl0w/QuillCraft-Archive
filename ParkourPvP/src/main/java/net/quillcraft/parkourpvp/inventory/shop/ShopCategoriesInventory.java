package net.quillcraft.parkourpvp.inventory.shop;

import net.quillcraft.core.utils.InventoryUtils;
import net.quillcraft.core.utils.builders.InventoryBuilder;
import net.quillcraft.parkourpvp.game.shop.ShopCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class ShopCategoriesInventory extends ShopInventory{

    public Inventory getInventory(Player player){
        final ShopCategory[] shopCategories = ShopCategory.values();
        final InventoryBuilder inventoryBuilder = new InventoryBuilder(InventoryUtils.getInventorySize(shopCategories.length), "Shop");

        Arrays.stream(shopCategories).parallel().forEach(shopCategory -> inventoryBuilder.setItem(shopCategory.getIndex(), shopCategory.getItemBuilder().toItemStack()));

        return inventoryBuilder.toInventory();
    }

}
