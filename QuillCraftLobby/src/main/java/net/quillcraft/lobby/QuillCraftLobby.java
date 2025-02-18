package net.quillcraft.lobby;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.lyflow.songapi.manager.SongManager;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.lobby.manager.PluginManager;
import net.quillcraft.lobby.manager.PluginMessageManager;
import net.quillcraft.lobby.manager.ScoreboardManager;
import net.quillcraft.lobby.npc.NPCManager;
import net.quillcraft.lobby.subscriber.ScoreboardSubscriber;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class QuillCraftLobby extends JavaPlugin implements Listener {

    private static QuillCraftLobby instance;

    private SongManager songManager;
    private NPCManager npcManager;
    private HeadDatabaseAPI headDatabaseAPI;
    private ProtocolManager protocolManager;
    private ScoreboardManager scoreboardManager;

    public static QuillCraftLobby getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        setInstance(this);

        saveDefaultConfig();

        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.npcManager = new NPCManager(this, 120);
        this.songManager = new SongManager(this);
        this.scoreboardManager = new ScoreboardManager();

        new PluginMessageManager(this);
        new ScoreboardSubscriber(this);

        QuillCraftCore.getInstance().getCommandManager().registerCommands(this, this.getFile());

        this.getServer().getPluginManager().registerEvents(this, this);

        new PluginManager(this);
    }

    @Override
    public void onDisable() {
        npcManager.onDisable();
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

    public SongManager getSongManager() {
        return songManager;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Nonnull
    @Override
    public PluginCommand getCommand(@Nonnull String name) {
        return Objects.requireNonNull(super.getCommand(name));
    }

    public HeadDatabaseAPI getHeadDatabaseAPI() {
        return headDatabaseAPI;
    }

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent event) {
        headDatabaseAPI = new HeadDatabaseAPI();
    }

    private static void setInstance(QuillCraftLobby instance) {
        QuillCraftLobby.instance = instance;
    }
}