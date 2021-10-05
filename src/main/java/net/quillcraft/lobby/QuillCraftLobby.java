package net.quillcraft.lobby;

import net.lyflow.songapi.manager.SongManager;
import net.quillcraft.lobby.manager.LanguageManager;
import net.quillcraft.lobby.manager.PluginManager;
import net.quillcraft.lobby.npc.NPCManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class QuillCraftLobby extends JavaPlugin {

    private static QuillCraftLobby INSTANCE;
    //private ProtocolManager protocolManager;

    private SongManager songManager;
    private NPCManager npcManager;

    @Override
    public void onEnable(){
        INSTANCE = this;

        saveDefaultConfig();

        LanguageManager.initAllLanguage(this);

        //this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.npcManager = new NPCManager(this,120);
        this.songManager = new SongManager(this);

        new PluginManager(this);
    }

    @Override
    public void onDisable(){
        npcManager.onDisable();
    }

    //public ProtocolManager getProtocolManager(){
      //  return protocolManager;
    //}

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
}