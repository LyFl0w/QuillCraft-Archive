package net.quillcraft.lobby.listener.player;

import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.ActionUtils;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.lobby.inventory.MenuInventory;
import net.quillcraft.lobby.inventory.VisibilityInventory;
import net.quillcraft.lobby.manager.ConfigurationManager;
import net.quillcraft.lobby.provider.HeadFinderProvider;
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

        // https://bukkit.org/threads/how-to-stop-breaking-crops-via-jump.362145/#post-3120378
        if(action == Action.PHYSICAL){
            if(event.getClickedBlock().getType() == Material.FARMLAND){
                event.setCancelled(true);
            }
            return;
        }

        if(action == Action.RIGHT_CLICK_BLOCK){
            final Block block = event.getClickedBlock();
            if(block.getType() == Material.PLAYER_HEAD) {
                final FileConfiguration headConfiguration = ConfigurationManager.HEAD.getConfiguration();
                ConfigurationSection configurationSection =  headConfiguration.getConfigurationSection(player.getLocation().getWorld().getName());
                for (int i = 0; i < configurationSection.getKeys(false).size(); i++) {
                    if(block.getX() == configurationSection.getInt( i +".x") && (block.getZ() == configurationSection.getInt( i +".z") && block.getY() == configurationSection.getInt( i +".y"))){
                        final HeadFinderProvider headFinderProvider = new HeadFinderProvider(player);
                        final List<Integer> list = headFinderProvider.getHeadlist();
                        if(!list.contains(i)){
                            list.add(i);
                            final int quillcoins = configurationSection.getInt(i + ".coins");
                            headFinderProvider.updateHeadList(player, quillcoins);

                            player.sendMessage("Nouvelle tête trouvée, vous gagnez " + quillcoins + " quillcoins" );
                        }else{
                            player.sendMessage("Tête déjà trouvée");
                        }

                        event.setCancelled(true);
                        return;
                    }
                }
            }

            //TODO : Anti Spamm door
            if(block.getBlockData() instanceof Door door){

                return;
            }
        }

        if(ActionUtils.hasRight(action)){
            final ItemStack item = event.getItem();

            if(item == null) {
                event.setCancelled(true);
                return;
            }
            //Check action with item into inventory
            switch(item.getType()){
                //Menu
                case FEATHER -> player.openInventory(new MenuInventory().getMenuInventory(LanguageManager.getLanguage(player)));
                //Boutique      Informations   Amis       Paramètres
                case GOLD_INGOT, PLAYER_HEAD, PUFFERFISH, COMPARATOR -> player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.WORKING_PROGRESS));
                //Particules
                case EXPERIENCE_BOTTLE -> {
                    player.updateInventory();
                    player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.WORKING_PROGRESS));
                }
                //Visibility
                default -> {
                    if(new ItemBuilder(item).isItemDye()){
                        try{
                            player.openInventory(new VisibilityInventory().getVisibilityInventory(new AccountProvider(player).getAccount()));
                        }catch(AccountNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            event.setCancelled(true);
            return;
        }

        // Else has left
        if(action == Action.LEFT_CLICK_BLOCK){
            if(event.getClickedBlock().getType() == Material.DRAGON_EGG){
                event.setCancelled(true);
            }
        }



    }
}