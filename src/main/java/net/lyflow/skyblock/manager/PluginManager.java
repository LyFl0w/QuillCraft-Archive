package net.lyflow.skyblock.manager;

import net.lyflow.skyblock.SkyBlock;
import net.lyflow.skyblock.command.IslandCommand;
import net.lyflow.skyblock.command.LobbyCommand;
import net.lyflow.skyblock.command.MoneyCommand;
import net.lyflow.skyblock.command.ShopCommand;
import net.lyflow.skyblock.listener.block.BlockSpreadListener;
import net.lyflow.skyblock.listener.inventory.InventoryClickListener;
import net.lyflow.skyblock.listener.player.AsyncPlayerPreLoginListener;
import net.lyflow.skyblock.listener.player.PlayerJoinListener;
import net.lyflow.skyblock.listener.player.PlayerQuitListener;
import net.lyflow.skyblock.listener.player.PlayerToggleSneakListener;

import org.bukkit.command.PluginCommand;

public class PluginManager {

    public PluginManager(SkyBlock skyblock) {
        registerEvents(skyblock, skyblock.getServer().getPluginManager());
        registerCommands(skyblock);
    }

    private void registerEvents(SkyBlock skyblock, org.bukkit.plugin.PluginManager pluginManager) {
        // PLAYER EVENT
        pluginManager.registerEvents(new PlayerQuitListener(skyblock), skyblock);
        pluginManager.registerEvents(new PlayerJoinListener(skyblock), skyblock);
        pluginManager.registerEvents(new AsyncPlayerPreLoginListener(skyblock), skyblock);
        pluginManager.registerEvents(new PlayerToggleSneakListener(), skyblock);

        // BLOCK EVENT
        pluginManager.registerEvents(new BlockSpreadListener(), skyblock);

        // INVENTORY EVENT
        pluginManager.registerEvents(new InventoryClickListener(skyblock), skyblock);
        //pluginManager.registerEvents(new CraftItemListener(skyblock), skyblock);
    }

    private void registerCommands(SkyBlock skyblock) {
        final IslandCommand islandCommand = new IslandCommand(skyblock);
        final PluginCommand islandPluginCommand = skyblock.getCommand("island");
        islandPluginCommand.setExecutor(islandCommand);
        islandPluginCommand.setTabCompleter(islandCommand);

        skyblock.getCommand("money").setExecutor(new MoneyCommand(skyblock));

        skyblock.getCommand("lobby").setExecutor(new LobbyCommand(skyblock));
        skyblock.getCommand("shop").setExecutor(new ShopCommand());
    }
}