package net.quillcraft.lobby.manager;

import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.command.HeadCommand;
import net.quillcraft.lobby.command.LobbyCommand;
import net.quillcraft.lobby.command.NPCCommand;
import net.quillcraft.lobby.command.SetLobbyCommand;
import net.quillcraft.lobby.listener.FoodLevelChangeListener;
import net.quillcraft.lobby.listener.WeatherChangeListener;
import net.quillcraft.lobby.listener.block.BlockBreakListener;
import net.quillcraft.lobby.listener.entity.EntityDamageListener;
import net.quillcraft.lobby.listener.entity.ExplosionPrimeListener;
import net.quillcraft.lobby.listener.entity.HangingBreakByEntityListener;
import net.quillcraft.lobby.listener.inventory.InventoryClickListener;
import net.quillcraft.lobby.listener.player.*;
import net.quillcraft.lobby.listener.player.custom.PlayerChangeLanguageListener;
import org.bukkit.plugin.messaging.Messenger;

public class PluginManager {

    private final QuillCraftLobby quillCraftLobby;

    public PluginManager(QuillCraftLobby quillCraftLobby) {
        this.quillCraftLobby = quillCraftLobby;
        registerEvents(quillCraftLobby.getServer().getPluginManager());
        registerPluginMessage(quillCraftLobby.getServer().getMessenger());
        registerCommands();
    }

    private void registerPluginMessage(Messenger messenger) {
        messenger.registerOutgoingPluginChannel(quillCraftLobby, "quillcraft:game");
    }

    private void registerEvents(org.bukkit.plugin.PluginManager pluginManager) {
        pluginManager.registerEvents(new BlockBreakListener(), quillCraftLobby);

        pluginManager.registerEvents(new PlayerJoinListener(quillCraftLobby), quillCraftLobby);
        pluginManager.registerEvents(new PlayerQuitListener(quillCraftLobby), quillCraftLobby);
        pluginManager.registerEvents(new PlayerItemListener(), quillCraftLobby);
        pluginManager.registerEvents(new PlayerInteractListener(), quillCraftLobby);
        pluginManager.registerEvents(new PlayerGameModeChangeListener(), quillCraftLobby);
        pluginManager.registerEvents(new PlayerInteractEntityListener(), quillCraftLobby);

        pluginManager.registerEvents(new EntityDamageListener(), quillCraftLobby);
        pluginManager.registerEvents(new HangingBreakByEntityListener(), quillCraftLobby);
        pluginManager.registerEvents(new ExplosionPrimeListener(), quillCraftLobby);

        pluginManager.registerEvents(new InventoryClickListener(quillCraftLobby), quillCraftLobby);

        pluginManager.registerEvents(new WeatherChangeListener(), quillCraftLobby);
        pluginManager.registerEvents(new FoodLevelChangeListener(), quillCraftLobby);

        pluginManager.registerEvents(new PlayerChangeLanguageListener(quillCraftLobby), quillCraftLobby);
    }

    private void registerCommands() {
        quillCraftLobby.getCommand("lobby").setExecutor(new LobbyCommand());
        quillCraftLobby.getCommand("setlobby").setExecutor(new SetLobbyCommand());
        quillCraftLobby.getCommand("npc").setExecutor(new NPCCommand(quillCraftLobby));
        quillCraftLobby.getCommand("head").setExecutor(new HeadCommand(quillCraftLobby));
    }

}
