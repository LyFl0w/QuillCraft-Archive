package net.quillcraft.lobby.listener.inventory;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.game.GameEnum;
import net.quillcraft.commons.game.Waiter;
import net.quillcraft.commons.game.WaitingList;
import net.quillcraft.commons.party.PartyProvider;
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
                if(account.hasParty() && !new PartyProvider(account).getParty().getOwnerUUID().equals(player.getUniqueId())){
                    player.sendMessage("§cVous devez être l'owner de votre party pour pouvoir lancer un jeu");
                    return;
                }

                final WaitingList waitingList = new WaitingList(GameItemToGameEnum.valueOf(item.getType().name()).getGameEnum());
                if(waitingList.getWaitersList().size() != 0){
                    waitingList.getWaitersList().add(new Waiter(player.getUniqueId(), account.hasParty()));
                    waitingList.updateWaitersListRedis();
                    return;
                }
                //TODO : ADD PLAYER AND HIS GROUP TO THE GAME IF A GAME EXIST
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

        }catch(AccountNotFoundException | PartyNotFoundException e){
            e.printStackTrace();
        }
    }

    private enum GameItemToGameEnum{

        IRON_BOOTS(GameEnum.PARKOUR_PVP_SOLO);

        private final GameEnum gameEnum;
        GameItemToGameEnum(GameEnum gameEnum){
            this.gameEnum = gameEnum;
        }

        public GameEnum getGameEnum(){
            return gameEnum;
        }
    }

}