package net.quillcraft.parkourpvp.inventory.shop;

import net.quillcraft.core.utils.builders.InventoryBuilder;
import net.quillcraft.parkourpvp.game.shop.ShopCategory;
import net.quillcraft.parkourpvp.game.shop.items.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class SubShopInventory extends ShopInventory{

    protected final String inventoryName;
    protected final ShopCategory shopCategory;
    public SubShopInventory(ShopCategory shopCategory){
        this.shopCategory = shopCategory;
        this.inventoryName = "Shop/"+shopCategory.getCategoryName();
    }

    @Override
    public Inventory getInventory(Player player){
        final Supplier<Stream<ShopItem>> shopItemStream = this::getShopItems;
        final InventoryBuilder inventoryBuilder = new InventoryBuilder(27, inventoryName);

        shopItemStream.get().parallel().forEach(shopItem -> inventoryBuilder.setItem(shopItem.getIndex(), shopItem.getItemStack()));

        return inventoryBuilder.toInventory();
    }

    protected Stream<ShopItem> getShopItems(){
        return Arrays.stream(ShopItem.values()).filter(shopItem -> shopItem.getShopCategory() == shopCategory);
    }

}
