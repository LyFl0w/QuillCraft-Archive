package net.quillcraft.parkourpvp.game.shop.items;

import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.parkourpvp.game.shop.ShopCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Arrays;

public enum ShopItem{

    // TOOLS
    WOODEN_PICKAXE(new ItemBuilder(Material.WOODEN_PICKAXE), 1, 2, ShopCategory.TOOLS),
    IRON_PICKAXE(new ItemBuilder(Material.IRON_PICKAXE), 3, 5, ShopCategory.TOOLS),
    DIAMOND_PICKAXE(new ItemBuilder(Material.DIAMOND_PICKAXE), 5, 7, ShopCategory.TOOLS),
    NETHERITE_PICKAXE(new ItemBuilder(Material.NETHERITE_PICKAXE), 7, 10, ShopCategory.TOOLS),
    WOODEN_SWORD(new ItemBuilder(Material.WOODEN_SWORD), 19, 2, ShopCategory.TOOLS),
    IRON_SWORD(new ItemBuilder(Material.IRON_SWORD), 21, 5, ShopCategory.TOOLS),
    DIAMOND_SWORD(new ItemBuilder(Material.DIAMOND_SWORD), 23, 7, ShopCategory.TOOLS),
    NETHERITE_SWORD(new ItemBuilder(Material.NETHERITE_SWORD), 25, 10, ShopCategory.TOOLS),
    SHIELD(new ItemBuilder(Material.SHIELD), 13, 5, ShopCategory.TOOLS),


    //Armor
    LEATHER_HELMET(new ItemBuilder(Material.LEATHER_HELMET), 0, 2, ShopCategory.ARMOR),
    IRON_HELMET(new ItemBuilder(Material.IRON_HELMET), 5, 5, ShopCategory.ARMOR),
    DIAMOND_HELMET(new ItemBuilder(Material.DIAMOND_HELMET), 18, 7, ShopCategory.ARMOR),
    NETHERITE_HELMET(new ItemBuilder(Material.NETHERITE_HELMET), 23, 10, ShopCategory.ARMOR),

    LEATHER_CHESTPLATE(new ItemBuilder(Material.LEATHER_CHESTPLATE), 1, 4, ShopCategory.ARMOR),
    IRON_CHESTPLATE(new ItemBuilder(Material.IRON_CHESTPLATE), 6, 7, ShopCategory.ARMOR),
    DIAMOND_CHESTPLATE(new ItemBuilder(Material.DIAMOND_CHESTPLATE), 19, 9, ShopCategory.ARMOR),
    NETHERITE_CHESTPLATE(new ItemBuilder(Material.NETHERITE_CHESTPLATE), 24, 12, ShopCategory.ARMOR),

    LEATHER_LEGGINGS(new ItemBuilder(Material.LEATHER_LEGGINGS), 2, 3, ShopCategory.ARMOR),
    IRON_LEGGINGS(new ItemBuilder(Material.IRON_LEGGINGS), 7, 6, ShopCategory.ARMOR),
    DIAMOND_LEGGINGS(new ItemBuilder(Material.DIAMOND_LEGGINGS), 20, 8, ShopCategory.ARMOR),
    NETHERITE_LEGGINGS(new ItemBuilder(Material.NETHERITE_LEGGINGS), 25, 11, ShopCategory.ARMOR),

    LEATHER_BOOTS(new ItemBuilder(Material.LEATHER_BOOTS), 3, 2, ShopCategory.ARMOR),
    IRON_BOOTS(new ItemBuilder(Material.IRON_BOOTS), 8, 5, ShopCategory.ARMOR),
    DIAMOND_BOOTS(new ItemBuilder(Material.DIAMOND_BOOTS), 21, 7, ShopCategory.ARMOR),
    NETHERITE_BOOTS(new ItemBuilder(Material.NETHERITE_BOOTS), 26, 10, ShopCategory.ARMOR),


    //Archer
    BOW(new ItemBuilder(Material.BOW), 11, 3, ShopCategory.ARCHER),
    ARROW(new ItemBuilder(Material.ARROW, 10), 13, 7, ShopCategory.ARCHER),
    CROSSBOW(new ItemBuilder(Material.CROSSBOW), 15, 3, ShopCategory.ARCHER),


    //Utilitaires
    GOLDEN_APPLE(new ItemBuilder(Material.GOLDEN_APPLE), 10, 3, ShopCategory.UTILITAIRES),
    LAVA_BUCKET(new ItemBuilder(Material.LAVA_BUCKET), 12, 3, ShopCategory.UTILITAIRES),
    WATER_BUCKET(new ItemBuilder(Material.WATER_BUCKET), 14, 2, ShopCategory.UTILITAIRES),
    ENDER_PEARL(new ItemBuilder(Material.ENDER_PEARL), 13, 3, ShopCategory.UTILITAIRES),
    EXPERIENCE_BOTTLE(new ItemBuilder(Material.EXPERIENCE_BOTTLE, 5), 16, 3, ShopCategory.UTILITAIRES),


    //Potions
    HEALING_POTION(new ItemBuilder(Material.POTION).setPotionData(new PotionData(PotionType.INSTANT_HEAL, false, false)), 10, 3, ShopCategory.POTIONS),
    JUMPING_POTION(new ItemBuilder(Material.POTION).setPotionData(new PotionData(PotionType.JUMP, false, false)), 12, 3, ShopCategory.POTIONS),
    POISONED_POTION(new ItemBuilder(Material.SPLASH_POTION).setPotionData(new PotionData(PotionType.POISON, false, false)), 14, 5, ShopCategory.POTIONS),
    REGENERATION_POTION(new ItemBuilder(Material.SPLASH_POTION).setPotionData(new PotionData(PotionType.REGEN, false, false)), 16, 5, ShopCategory.POTIONS),

    STONE(new ItemBuilder(Material.STONE, 16), 13, 1, ShopCategory.BLOCKS);

    private final ItemStack itemStack;
    private final int index;
    private final int price;
    private final ShopCategory shopCategory;

    ShopItem(ItemBuilder itemBuilder, int index, int price, ShopCategory shopCategory){
        this.itemStack = itemBuilder.setLore("§6"+price+" Coins").toItemStack();
        this.index = index;
        this.price = price;
        this.shopCategory = shopCategory;
    }

    public ItemStack getItemStack(){
        return itemStack;
    }

    public int getIndex(){
        return index;
    }

    public int getPrice(){
        return price;
    }

    public ShopCategory getShopCategory(){
        return shopCategory;
    }

    public static ShopItem getShopItem(Material material){
        return Arrays.stream(values()).parallel().filter(shopItem -> shopItem.itemStack.getType() == material).findFirst().get();
    }

}
