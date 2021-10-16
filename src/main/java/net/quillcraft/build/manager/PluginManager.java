package net.quillcraft.build.manager;

import net.quillcraft.build.QuillCraftBuild;
import net.quillcraft.build.command.FlightSpeed;
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
        main.getCommand("flightspeed").setExecutor(new FlightSpeed());
    }

}
