package net.quillcraft.highblock.inventory.shop;

import net.quillcraft.highblock.shop.ShopCategory;
import net.quillcraft.highblock.utils.builder.InventoryBuilder;
import net.quillcraft.highblock.utils.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class ShopCategoryInventory {

    public static Inventory getShopCategoryInventory(boolean isBuyInventory) {
        final InventoryBuilder inventoryBuilder = new InventoryBuilder(9, "§aShop/Category/"+(isBuyInventory ? "Buy" : "Sell"))
                .setItem(0, new ItemBuilder(Material.PAPER).setName("§9Back").toItemStack());
        Arrays.stream(ShopCategory.values()).forEach(shopCategory -> inventoryBuilder.setItem(shopCategory.getPos(), shopCategory.getItemStack()));
        
        return inventoryBuilder.toInventory();
    }

}
