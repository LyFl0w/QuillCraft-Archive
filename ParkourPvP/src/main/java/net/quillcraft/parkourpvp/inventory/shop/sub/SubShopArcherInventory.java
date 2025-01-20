package net.quillcraft.parkourpvp.inventory.shop.sub;

import net.quillcraft.parkourpvp.game.shop.ShopCategory;
import net.quillcraft.parkourpvp.inventory.shop.SubShopInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SubShopArcherInventory extends SubShopInventory{

    public SubShopArcherInventory(){
        super(ShopCategory.ARCHER);
    }

    @Override
    public Inventory getInventory(Player player){
        return super.getInventory(player);
    }
}
