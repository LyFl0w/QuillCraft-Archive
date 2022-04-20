package net.quillcraft.lobby.manager;

import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.command.HeadCommand;
import net.quillcraft.lobby.command.LobbyCommand;
import net.quillcraft.lobby.command.NPCCommand;
import net.quillcraft.lobby.command.SetLobbyCommand;
import net.quillcraft.lobby.listener.FoodLevelChangeListener;
import net.quillcraft.lobby.listener.WeatherChangeListener;
import net.quillcraft.lobby.listener.entity.EntityDamageListener;
import net.quillcraft.lobby.listener.entity.ExplosionPrimeListener;
import net.quillcraft.lobby.listener.inventory.InventoryClickListener;
import net.quillcraft.lobby.listener.player.*;
import net.quillcraft.lobby.listener.player.PlayerChangeLanguageListener;

public class PluginManager {

    //private final TaskManager taskManager;
    private final QuillCraftLobby main;
    public PluginManager(QuillCraftLobby main){
        this.main = main;
        //this.taskManager = new TaskManager(main);
        registerEvents(main.getServer().getPluginManager());
        registerCommands();
    }

    private void registerEvents(org.bukkit.plugin.PluginManager pluginManager){
        pluginManager.registerEvents(new PlayerJoinListener(main), main);
        pluginManager.registerEvents(new PlayerQuitListener(), main);
        pluginManager.registerEvents(new PlayerItemListener(), main);
        pluginManager.registerEvents(new PlayerInteractListener(), main);
        pluginManager.registerEvents(new PlayerItemHeldListener(), main);
        pluginManager.registerEvents(new PlayerGameModeChangeListener(), main);

        pluginManager.registerEvents(new EntityDamageListener(), main);
        pluginManager.registerEvents(new ExplosionPrimeListener(), main);

        pluginManager.registerEvents(new InventoryClickListener(main), main);

        pluginManager.registerEvents(new WeatherChangeListener(), main);
        pluginManager.registerEvents(new FoodLevelChangeListener(), main);

        pluginManager.registerEvents(new PlayerChangeLanguageListener(), main);
    }

    private void registerCommands(){
        main.getCommand("lobby").setExecutor(new LobbyCommand());
        main.getCommand("setlobby").setExecutor(new SetLobbyCommand());
        main.getCommand("npc").setExecutor(new NPCCommand(main));
        main.getCommand("head").setExecutor(new HeadCommand(main));
    }

    /*public void onDisable(){
        taskManager.onDisableTasks();
    }*/

}
