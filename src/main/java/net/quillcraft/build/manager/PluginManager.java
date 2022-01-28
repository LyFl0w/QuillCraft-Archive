package net.quillcraft.build.manager;

import net.quillcraft.build.QuillCraftBuild;
import net.quillcraft.build.command.CreateWarpCommand;
import net.quillcraft.build.command.FlightSpeedCommand;
import net.quillcraft.build.command.RemoveWarpCommand;
import net.quillcraft.build.command.WarpCommand;
import net.quillcraft.build.command.completion.WarpTabCompletion;

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
    }



    private void registerCommands(){
        main.getCommand("flightspeed").setExecutor(new FlightSpeedCommand());

        final WarpTabCompletion warpTabCompletion = new WarpTabCompletion();
        main.getCommand("warp").setExecutor(new WarpCommand());
        main.getCommand("warp").setTabCompleter(warpTabCompletion);

        main.getCommand("createwarp").setExecutor(new CreateWarpCommand());
        main.getCommand("createwarp").setTabCompleter(warpTabCompletion);

        main.getCommand("removewarp").setExecutor(new RemoveWarpCommand());
        main.getCommand("removewarp").setTabCompleter(warpTabCompletion);
    }

}
