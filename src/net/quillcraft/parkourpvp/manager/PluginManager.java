package net.quillcraft.parkourpvp.manager;

import net.quillcraft.parkourpvp.ParkourPvP;

import org.bukkit.Server;
import org.bukkit.plugin.messaging.Messenger;

public class PluginManager{

    private final ParkourPvP main;
    public PluginManager(ParkourPvP main){
        this.main = main;

        final Server server = main.getServer();
        registerEvents(server.getPluginManager());
        registerPluginMessage(server.getMessenger());
        registerCommands();
    }

    private void registerEvents(org.bukkit.plugin.PluginManager pluginManager){}

    private void registerPluginMessage(Messenger messenger){}

    private void registerCommands(){}

}
