package net.quillcraft.lobby;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.lyflow.songapi.manager.SongManager;
import net.quillcraft.lobby.manager.PluginManager;
import net.quillcraft.lobby.npc.NPCManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class QuillCraftLobby extends JavaPlugin implements Listener {

    private static QuillCraftLobby INSTANCE;
    //private ProtocolManager protocolManager;

    private SongManager songManager;
    private NPCManager npcManager;
    private HeadDatabaseAPI headDatabaseAPI;

    @Override
    public void onEnable(){
        INSTANCE = this;

        saveDefaultConfig();

        //this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.npcManager = new NPCManager(this,120);
        this.songManager = new SongManager(this);
        this.getServer().getPluginManager().registerEvents(this, this);

        new PluginManager(this);
    }

    @Override
    public void onDisable(){
        npcManager.onDisable();
    }

    public static QuillCraftLobby getInstance(){
        return INSTANCE;
    }

    public NPCManager getNpcManager(){
        return npcManager;
    }

    public SongManager getSongManager(){
        return songManager;
    }

    @Nonnull
    public PluginCommand getCommand(@Nonnull String name){
        return Objects.requireNonNull(super.getCommand(name));
    }

    public HeadDatabaseAPI getHeadDatabaseAPI() {
        return headDatabaseAPI;
    }

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent e) {
        headDatabaseAPI = new HeadDatabaseAPI();
    }

}