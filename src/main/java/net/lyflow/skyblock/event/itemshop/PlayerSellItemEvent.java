package net.lyflow.skyblock.event.itemshop;

import net.lyflow.skyblock.SkyBlock;
import net.lyflow.skyblock.database.request.account.AccountRequest;
import net.lyflow.skyblock.shop.ItemShop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class PlayerSellItemEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCancelled = false;

    public PlayerSellItemEvent(SkyBlock skyBlock, Player player, ItemShop itemShop, int amount) {
        final AccountRequest accountRequest = new AccountRequest(skyBlock.getDatabase(), false);
        try {
            final ItemStack itemStack = new ItemStack(itemShop.getMaterial(), amount);

            if(!player.getInventory().contains(itemShop.getMaterial(), amount)) {
                player.sendMessage("Vous n'avez pas "+amount+" "+itemStack.getItemMeta().getDisplayName()+ " à vendre");
                setCancelled(true);
                return;
            }
            accountRequest.setMoney(player.getUniqueId(), accountRequest.getMoney(player.getUniqueId())+itemShop.getSellPrice() * amount);
            skyBlock.getDatabase().closeConnection();

            removeItems(player, itemShop.getMaterial(), amount);
            player.sendMessage("Vous avez vendu "+amount+" "+itemStack.getItemMeta().getDisplayName());
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean setCancelled) {
        this.isCancelled = setCancelled;
    }

    public void removeItems(Player player, Material material, int amount) {
        final PlayerInventory playerInventory = player.getInventory();

        for(int slot=0; slot<playerInventory.getSize(); slot++) {
            final ItemStack itemStack = playerInventory.getItem(slot);
            if(itemStack == null || itemStack.getType() != material) continue;

            amount -= itemStack.getAmount();
            if(amount < 0)  {
                itemStack.setAmount(-amount);
                return;
            }
            playerInventory.remove(itemStack);
            if(amount == 0) return;
        }
    }

}
