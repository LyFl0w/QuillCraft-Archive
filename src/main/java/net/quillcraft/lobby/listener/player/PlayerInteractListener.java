package net.quillcraft.lobby.listener.player;

import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.ActionUtils;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.lobby.headfinder.HeadFinderProvider;
import net.quillcraft.lobby.inventory.MenuInventory;
import net.quillcraft.lobby.inventory.VisibilityInventory;
import net.quillcraft.lobby.manager.ConfigurationManager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.lumy.api.text.Text;

import java.util.List;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        final Player player = event.getPlayer();
        final Action action = event.getAction();


        if(event.getHand() == EquipmentSlot.OFF_HAND) return;

        if(action == Action.RIGHT_CLICK_BLOCK){
            final Block clickedBlock = event.getClickedBlock();
            if(clickedBlock.getType() == Material.PLAYER_HEAD) {
                final FileConfiguration headConfiguration = ConfigurationManager.HEAD.getConfiguration();
                ConfigurationSection configurationSection =  headConfiguration.getConfigurationSection(player.getLocation().getWorld().getName());
                for (int i = 0; i < configurationSection.getKeys(false).size(); i++) {
                    if(clickedBlock.getX() == configurationSection.getInt( i +".x") && (clickedBlock.getZ() == configurationSection.getInt( i +".z") && clickedBlock.getY() == configurationSection.getInt( i +".y"))){
                        final HeadFinderProvider headFinderProvider = new HeadFinderProvider(player);
                        final List<Integer> list = headFinderProvider.getHeadlist();
                        if(!list.contains(i)){
                            list.add(i);
                            headFinderProvider.updateHeadList();
                            player.sendMessage("Nouvelle tête trouvée");
                        }else{
                            player.sendMessage("Tête déjà trouvée");
                        }
                        break;
                    }
                }
                return;
            }
        }

        if(ActionUtils.hasRight(action)){
            final ItemStack item = event.getItem();

            if(item == null) return;
            //Check action with item into inventory
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
        //TODO : Anti Spamm door
        if(action == Action.LEFT_CLICK_BLOCK){
            final Block block = event.getClickedBlock();
            if(block.getBlockData() instanceof Door door && door.isOpen()){

            }
        }
    }
}