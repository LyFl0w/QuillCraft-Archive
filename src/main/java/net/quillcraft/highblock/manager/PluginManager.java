package net.quillcraft.highblock.manager;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.command.*;
import net.quillcraft.highblock.listener.block.BlockSpreadListener;
import net.quillcraft.highblock.listener.inventory.InventoryClickListener;
import net.quillcraft.highblock.listener.player.*;

import org.bukkit.command.PluginCommand;

public class PluginManager {

    public PluginManager(HighBlock skyblock) {
        registerEvents(skyblock, skyblock.getServer().getPluginManager());
        registerCommands(skyblock);
    }

    private void registerEvents(HighBlock skyblock, org.bukkit.plugin.PluginManager pluginManager) {
        // PLAYER EVENT
        pluginManager.registerEvents(new PlayerQuitListener(skyblock), skyblock);
        pluginManager.registerEvents(new PlayerJoinListener(skyblock), skyblock);
        pluginManager.registerEvents(new PlayerDeathListener(skyblock), skyblock);
        pluginManager.registerEvents(new PlayerRespawnListener(skyblock), skyblock);
        pluginManager.registerEvents(new PlayerInteractListener(), skyblock);

        pluginManager.registerEvents(new AsyncPlayerPreLoginListener(skyblock), skyblock);
        pluginManager.registerEvents(new PlayerToggleSneakListener(), skyblock);

        // BLOCK EVENT
        pluginManager.registerEvents(new BlockSpreadListener(), skyblock);

        // INVENTORY EVENT
        pluginManager.registerEvents(new InventoryClickListener(skyblock), skyblock);
        //pluginManager.registerEvents(new CraftItemListener(skyblock), skyblock);
    }

    private void registerCommands(HighBlock skyblock) {
        final IslandCommand islandCommand = new IslandCommand(skyblock);
        final PluginCommand islandPluginCommand = skyblock.getCommand("island");
        islandPluginCommand.setExecutor(islandCommand);
        islandPluginCommand.setTabCompleter(islandCommand);

        skyblock.getCommand("money").setExecutor(new MoneyCommand(skyblock));

        skyblock.getCommand("lobby").setExecutor(new LobbyCommand(skyblock));
        skyblock.getCommand("shop").setExecutor(new ShopCommand());
        skyblock.getCommand("challenge").setExecutor(new ChallengeCommand());


    }
}