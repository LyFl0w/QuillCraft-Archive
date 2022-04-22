package net.quillcraft.parkourpvp.manager;

import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.listener.block.BlockBreakListener;
import net.quillcraft.parkourpvp.listener.block.BlockPlaceListener;
import net.quillcraft.parkourpvp.listener.entity.EntityDamageListener;
import net.quillcraft.parkourpvp.listener.entity.EntityFoodLevelChangeListener;
import net.quillcraft.parkourpvp.listener.player.*;
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
        pluginManager.registerEvents(new PlayerMoveListener(main), main);
        pluginManager.registerEvents(new PlayerInteractListener(main), main);
        pluginManager.registerEvents(new PlayerJumpListener(main), main);

        pluginManager.registerEvents(new EntityDamageListener(main), main);
        pluginManager.registerEvents(new EntityFoodLevelChangeListener(main), main);

        final BlockPlaceListener blockPlaceListener = new BlockPlaceListener(main);
        pluginManager.registerEvents(blockPlaceListener, main);
        pluginManager.registerEvents(new BlockBreakListener(main, blockPlaceListener), main);

    }

    private void registerPluginMessage(Messenger messenger){}

    private void registerCommands(){}

}
