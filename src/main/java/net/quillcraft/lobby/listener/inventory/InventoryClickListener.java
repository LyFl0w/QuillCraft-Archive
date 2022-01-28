package net.quillcraft.lobby.listener.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.inventory.VisibilityInventory;
import net.quillcraft.lobby.listener.player.custom.PlayerVisibilityChangeEvent;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.lumy.api.text.Text;

public class InventoryClickListener implements Listener {

    private final QuillCraftLobby quillCraftLobby;
    public InventoryClickListener(QuillCraftLobby quillCraftLobby){
        this.quillCraftLobby = quillCraftLobby;
    }

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

            if(title.equals(languageManager.getMessage(Text.INVENTORY_NAME_MENU))){
                if(item.getType() == Material.IRON_BOOTS){

                    return;
                }
                return;
            }

            if(title.equals(languageManager.getMessage(Text.INVENTORY_NAME_VISIBILITY))){
                final PlayerVisibilityChangeEvent playerVisibilityChangeEvent =
                        new PlayerVisibilityChangeEvent(player, accountProvider, account, Account.Visibility.getVisibilityByData(item.getType()));
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