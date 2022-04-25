package net.quillcraft.lobby.listener.block;

import net.quillcraft.core.utils.Title;
import net.quillcraft.lobby.listener.player.PlayerInteractListener;
import net.quillcraft.lobby.manager.ConfigurationManager;
import net.quillcraft.lobby.provider.MuguetProvider;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.lumy.api.text.TextList;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        event.setCancelled(true);
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if(block.getType() == Material.LILY_OF_THE_VALLEY){
            block.setType(Material.AIR);
            final FileConfiguration muguetConfiguration = ConfigurationManager.MUGUET.getConfiguration();
            String time = String.valueOf(System.currentTimeMillis() + 600000);
            muguetConfiguration.createSection(time);
            ConfigurationSection section = muguetConfiguration.getConfigurationSection(time);
            section.set(String.valueOf(section.getKeys(false).size() + 1), block.getLocation());
            ConfigurationManager.MUGUET.saveFile();
            MuguetProvider muguetProvider = new MuguetProvider(player);
            int muguetCount = muguetProvider.getMuguetCount() + 1;
            muguetProvider.updateMuguetCounter(muguetCount);
            new Title(player).sendActionBar("Vous avez récupéré du muguet, vous avez dans votre sac "+ muguetCount + " muguets");
        }
    }
}















