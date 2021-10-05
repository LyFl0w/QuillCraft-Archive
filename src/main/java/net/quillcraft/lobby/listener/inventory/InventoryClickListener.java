package net.quillcraft.lobby.listener.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.inventory.VisibilityInventory;
import net.quillcraft.lobby.manager.LanguageManager;
import net.quillcraft.lobby.player.PlayerVisibilityChangeEvent;
import net.quillcraft.lobby.text.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public record InventoryClickListener(QuillCraftLobby quillCraftLobby) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getClickedInventory();
        final String title = event.getView().getTitle();
        final ItemStack item = event.getCurrentItem();

        if(inventory == null) return;

        event.setCancelled(true);

        if(item == null || item.getType() == Material.AIR) return;

        final AccountProvider accountProvider = new AccountProvider(player);
        try{
            final Account account = accountProvider.getAccount();
            final LanguageManager languageManager = LanguageManager.getLanguage(account);

            if(title.equals(languageManager.getMessage(Text.MENU_INVENTORY))){
                return;
            }

            if(title.equals(languageManager.getMessage(Text.VISIBILITY_INVENTORY))){
                final PlayerVisibilityChangeEvent playerVisibilityChangeEvent = new PlayerVisibilityChangeEvent(player, accountProvider, account, Account.Visibility.getVisibilityByData(item.getType()));
                quillCraftLobby.getServer().getPluginManager().callEvent(playerVisibilityChangeEvent);
                if(!playerVisibilityChangeEvent.isCancelled()){
                    player.openInventory(new VisibilityInventory().getVisibilityInventory(account));
                }
                return;
            }

        }catch(AccountNotFoundException e){
            e.printStackTrace();
        }
    }

}