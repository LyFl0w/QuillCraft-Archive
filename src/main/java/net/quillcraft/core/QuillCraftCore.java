package net.quillcraft.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.quillcraft.core.manager.CommandManager;
import net.quillcraft.core.manager.DataManager;
import net.quillcraft.core.manager.PluginManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class QuillCraftCore extends JavaPlugin {

    private static QuillCraftCore INSTANCE;

    private DataManager dataManager;
    private ProtocolManager protocolManager;
    private CommandManager commandManager;

    public static QuillCraftCore getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        protocolManager = ProtocolLibrary.getProtocolManager();

        commandManager = new CommandManager(this, this.getFile());
        dataManager = new DataManager(this);

        dataManager.init();

        new PluginManager(this);
    }

    @Override
    public void onDisable() {
        dataManager.close();
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    @Nonnull
    public PluginCommand getCommand(@Nonnull String name) {
        return Objects.requireNonNull(super.getCommand(name));
    }

}