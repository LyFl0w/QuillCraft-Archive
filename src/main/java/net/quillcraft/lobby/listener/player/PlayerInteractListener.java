package net.quillcraft.lobby.listener.player;

import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.event.action.ActualAction;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.lobby.inventory.MenuInventory;
import net.quillcraft.lobby.inventory.VisibilityInventory;
import net.quillcraft.lobby.manager.LanguageManager;
import net.quillcraft.lobby.text.Text;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        final Player player = event.getPlayer();
        final Action action = event.getAction();

        if(ActualAction.hasLeft(action)){
            final ItemStack item = event.getItem();

            if(item == null) return;
            //Check action with item into net.quillcraft.lobby.inventory
            switch(item.getType()){
                //Menu
                case FEATHER -> {
                    player.openInventory(new MenuInventory().getMenuInventory(LanguageManager.getLanguage(player)));
                    event.setCancelled(true);
                    return;
                }
                //Boutique      Informations   Amis       Paramètres
                case GOLD_INGOT, PLAYER_HEAD, PUFFERFISH, COMPARATOR -> {
                    player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.WORKING_PROGRESS));
                    event.setCancelled(true);
                    return;
                }
                //Particules
                case EXPERIENCE_BOTTLE -> {
                    player.updateInventory();
                    player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.WORKING_PROGRESS));
                    event.setCancelled(true);
                    return;
                }
                //Visibility
                default -> {
                    System.out.println("default");
                    if(new ItemBuilder(item).isItemDye()){
                        try{
                            player.openInventory(new VisibilityInventory().getVisibilityInventory(new AccountProvider(player).getAccount()));
                        }catch(AccountNotFoundException e){
                            e.printStackTrace();
                        }finally{
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
        //Anti Spamm door
        if(action == Action.LEFT_CLICK_BLOCK){
            final Block block = event.getClickedBlock();
            if(block.getBlockData() instanceof Door door && door.isOpen()){

            }
        }
    }
}