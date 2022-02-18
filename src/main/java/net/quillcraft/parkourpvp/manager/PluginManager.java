package net.quillcraft.parkourpvp.manager;

import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.listener.player.PlayerJoinListener;

import net.quillcraft.parkourpvp.listener.player.PlayerQuitListener;
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

    private void registerEvents(org.bukkit.plugin.PluginManager pluginManager){
        pluginManager.registerEvents(new PlayerJoinListener(main), main);
        pluginManager.registerEvents(new PlayerQuitListener(main), main);
    }

    private void registerPluginMessage(Messenger messenger){}

    private void registerCommands(){}

}
