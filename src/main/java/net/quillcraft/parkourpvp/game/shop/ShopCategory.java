package net.quillcraft.parkourpvp.game.shop;

import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.parkourpvp.inventory.shop.SubShopInventory;
import net.quillcraft.parkourpvp.inventory.shop.sub.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public enum ShopCategory{

    TOOLS(new ItemBuilder(Material.DIAMOND_SWORD), "Fighting Tools", 0, SubShopToolsInventory.class),
    ARCHER(new ItemBuilder(Material.BOW), "Archer", 1, SubShopArcherInventory.class),
    ARMOR(new ItemBuilder(Material.NETHERITE_CHESTPLATE), "Armor", 3, SubShopArmorInventory.class),
    UTILITAIRES(new ItemBuilder(Material.GOLDEN_APPLE), "Utilitaires", 5, SubShopUtilitairesInventory.class),
    POTIONS(new ItemBuilder(Material.POTION).setPotionData(new PotionData(PotionType.INSTANT_HEAL, false, false)), "Potions", 7, SubShopPotionsInventory.class),
    BLOCKS(new ItemBuilder(Material.STONE), "Blocks", 8, SubShopBlocksInventory.class);

    private final Class<? extends SubShopInventory> shopInventoryClass;
    private final ItemBuilder itemBuilder;
    private final String categoryName;
    private final int index;

    ShopCategory(ItemBuilder itemBuilder, String categoryName, int index, Class<? extends SubShopInventory> shopInventoryClass){
        this.categoryName = categoryName;
        this.itemBuilder = itemBuilder.setName(categoryName);
        this.index = index;
        this.shopInventoryClass = shopInventoryClass;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public ItemBuilder getItemBuilder(){
        return itemBuilder;
    }

    public int getIndex(){
        return index;
    }

    public Inventory getInventory(Player player){
        try{
            return (Inventory) shopInventoryClass.getDeclaredMethod("getInventory", Player.class).invoke(shopInventoryClass.getConstructor().newInstance(), player);
        }catch(IllegalAccessException|InvocationTargetException|NoSuchMethodException|InstantiationException e){
            e.printStackTrace();
        }
        return null;
    }

    public static ShopCategory getCategory(Material material){
        return Arrays.stream(values()).parallel().filter(shopCategory -> shopCategory.itemBuilder.toItemStack().getType() == material).findFirst().get();
    }

}
