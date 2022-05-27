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

    private ProtocolManager protocolManager;
    private CommandManager commandManager;


    @Override
    public void onEnable(){
        INSTANCE = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        commandManager = new CommandManager(this, this.getFile());

        DataManager.initAllData(this);

        new PluginManager(this);
    }

    @Override
    public void onDisable(){
        DataManager.closeAllData();
    }

    public static QuillCraftCore getInstance(){
        return INSTANCE;
    }

    public ProtocolManager getProtocolManager(){
        return protocolManager;
    }

    public CommandManager getCommandManager(){
        return commandManager;
    }

    @Nonnull
    public PluginCommand getCommand(@Nonnull String name){
        return Objects.requireNonNull(super.getCommand(name));
    }

}