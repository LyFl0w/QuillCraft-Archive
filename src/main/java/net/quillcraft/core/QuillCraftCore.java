package net.quillcraft.core;

import net.quillcraft.core.manager.DataManager;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.manager.PluginManager;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class QuillCraftCore extends JavaPlugin {

    private static QuillCraftCore INSTANCE;
    //private static ProtocolManager protocolManager;

    @Override
    public void onEnable(){
        INSTANCE = this;
        //protocolManager = ProtocolLibrary.getProtocolManager();

        DataManager.initAllData();
        LanguageManager.initAllLanguage(this);

        new PluginManager(this);
    }

    @Override
    public void onDisable(){
        DataManager.closeAllData();
    }

    //public static ProtocolManager getProtocolManager(){
      //  return protocolManager;
    //}

    public static QuillCraftCore getInstance(){
        return INSTANCE;
    }

    @Nonnull
    public PluginCommand getCommand(@Nonnull String name){
        return Objects.requireNonNull(super.getCommand(name));
    }
}
