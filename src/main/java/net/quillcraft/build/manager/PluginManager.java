package net.quillcraft.build.manager;

import net.quillcraft.build.QuillCraftBuild;
import net.quillcraft.build.command.CreateWarpCommand;
import net.quillcraft.build.command.FlightSpeedCommand;
import net.quillcraft.build.command.WarpCommand;
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

        final WarpCommand warpCommand = new WarpCommand();
        main.getCommand("warp").setExecutor(warpCommand);
        main.getCommand("warp").setTabCompleter(warpCommand);

        final CreateWarpCommand createWarpCommand = new CreateWarpCommand();
        main.getCommand("createwarp").setExecutor(createWarpCommand);
        main.getCommand("createwarp").setTabCompleter(createWarpCommand);
    }

}
