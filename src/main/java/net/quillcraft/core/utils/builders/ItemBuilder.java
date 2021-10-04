package net.quillcraft.core.utils.builders;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public record ItemBuilder(ItemStack itemStack) {

    public ItemBuilder(final Material material){
        this(material, 1);
    }

    public ItemBuilder(final Material material, final int amount){
        this(new ItemStack(material, amount));
    }

    @Deprecated
    public ItemBuilder(final Material material, final byte meta){
        this(new ItemStack(material, 1, meta));
    }

    @Deprecated
    public ItemBuilder(final Material material, final int amount, final short meta){
        this(new ItemStack(material, amount, meta));
    }

    public ItemBuilder(@Nonnull final ItemStack itemStack){
        this.itemStack = itemStack;
    }

    public ItemBuilder clone(){
        return new ItemBuilder(itemStack);
    }

    public String getName(){
        return itemStack.getItemMeta().getDisplayName();
    }

    public ItemBuilder setDamage(final int damage){
        final Damageable im = (Damageable)itemStack.getItemMeta();
        im.setDamage(damage);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setName(final String name){
        final ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(name);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(final Enchantment ench, final int level){
        itemStack.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder removeEnchantment(final Enchantment ench){
        itemStack.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder setSkullOwner(final OfflinePlayer offlinePlayer){
        final SkullMeta im = (SkullMeta) itemStack.getItemMeta();
        im.setOwningPlayer(offlinePlayer);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchant(final Enchantment ench, final int level){
        final ItemMeta im = itemStack.getItemMeta();
        im.addEnchant(ench, level, true);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(final List<String> lore){
        final ItemMeta im = itemStack.getItemMeta();
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(final String... lore){
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags){
        final ItemMeta im = itemStack.getItemMeta();
        im.addItemFlags(itemFlags);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addTextPage(final int page, final String text){
        final BookMeta bm = (BookMeta) itemStack.getItemMeta();
        bm.setPage(page, bm.getPage(page) + text);
        itemStack.setItemMeta(bm);
        return this;
    }

    public ItemBuilder setUnbreakable(){
        final ItemMeta im = itemStack.getItemMeta();
        im.setUnbreakable(true);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLeatherArmorColor(final Color color){
        final LeatherArmorMeta im = (LeatherArmorMeta) itemStack.getItemMeta();
        im.setColor(color);
        itemStack.setItemMeta(im);
        return this;
    }

    public boolean isItemDye(){
        return switch(itemStack.getType()){
            case BLACK_DYE, BLUE_DYE, BROWN_DYE, CYAN_DYE, GRAY_DYE, GREEN_DYE, LIGHT_BLUE_DYE, LIME_DYE, LIGHT_GRAY_DYE, MAGENTA_DYE, ORANGE_DYE, PINK_DYE, PURPLE_DYE, RED_DYE, WHITE_DYE, YELLOW_DYE -> true;
            default -> false;
        };
    }

    public ItemStack toItemStack(){
        return itemStack;
    }


}