package net.quillcraft.highblock.manager;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.command.*;
import net.quillcraft.highblock.listener.block.BlockSpreadListener;
import net.quillcraft.highblock.listener.inventory.InventoryClickListener;
import net.quillcraft.highblock.listener.player.*;
import org.bukkit.command.PluginCommand;

public class PluginManager {

    public PluginManager(HighBlock highblock) {
        registerEvents(highblock, highblock.getServer().getPluginManager());
        registerCommands(highblock);
    }

    private void registerEvents(HighBlock highblock, org.bukkit.plugin.PluginManager pluginManager) {
        // PLAYER EVENT
        pluginManager.registerEvents(new PlayerQuitListener(highblock), highblock);
        pluginManager.registerEvents(new PlayerJoinListener(highblock), highblock);
        pluginManager.registerEvents(new PlayerDeathListener(highblock), highblock);
        pluginManager.registerEvents(new PlayerRespawnListener(highblock), highblock);
        pluginManager.registerEvents(new PlayerInteractListener(), highblock);

        pluginManager.registerEvents(new AsyncPlayerPreLoginListener(highblock), highblock);
        pluginManager.registerEvents(new PlayerToggleSneakListener(), highblock);

        // BLOCK EVENT
        pluginManager.registerEvents(new BlockSpreadListener(), highblock);

        // INVENTORY EVENT
        pluginManager.registerEvents(new InventoryClickListener(highblock), highblock);
        //pluginManager.registerEvents(new CraftItemListener(highblock), highblock);
    }

    private void registerCommands(HighBlock highblock) {
        final IslandCommand islandCommand = new IslandCommand(highblock);
        final PluginCommand islandPluginCommand = highblock.getCommand("island");
        islandPluginCommand.setExecutor(islandCommand);
        islandPluginCommand.setTabCompleter(islandCommand);

        highblock.getCommand("money").setExecutor(new MoneyCommand(highblock));

        highblock.getCommand("lobby").setExecutor(new LobbyCommand(highblock));
        highblock.getCommand("shop").setExecutor(new ShopCommand());
        highblock.getCommand("challenge").setExecutor(new ChallengeCommand());


    }
}