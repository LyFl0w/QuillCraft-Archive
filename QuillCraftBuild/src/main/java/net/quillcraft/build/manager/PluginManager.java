package net.quillcraft.build.manager;

import net.quillcraft.build.QuillCraftBuild;
import net.quillcraft.build.command.*;
import net.quillcraft.build.command.completion.WarpTabCompletion;
import net.quillcraft.build.listener.player.PlayerJoinListener;

import org.bukkit.Server;

public class PluginManager {

    private final QuillCraftBuild main;
    public PluginManager(QuillCraftBuild main){
        this.main = main;
        final Server server = main.getServer();
        registerEvents(server.getPluginManager());
        registerCommands();
    }

    private void registerEvents(org.bukkit.plugin.PluginManager pluginManager){
        pluginManager.registerEvents(new PlayerJoinListener(main), main);
    }

    private void registerCommands(){
        main.getCommand("flightspeed").setExecutor(new FlightSpeedCommand());
        main.getCommand("inventory").setExecutor(new InventoryCommand());


        final WarpTabCompletion warpTabCompletion = new WarpTabCompletion();
        main.getCommand("warp").setExecutor(new WarpCommand());
        main.getCommand("warp").setTabCompleter(warpTabCompletion);

        main.getCommand("createwarp").setExecutor(new CreateWarpCommand());
        main.getCommand("createwarp").setTabCompleter(warpTabCompletion);

        main.getCommand("removewarp").setExecutor(new RemoveWarpCommand());
        main.getCommand("removewarp").setTabCompleter(warpTabCompletion);
    }

}
