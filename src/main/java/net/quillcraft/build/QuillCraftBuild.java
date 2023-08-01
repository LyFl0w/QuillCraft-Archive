package net.quillcraft.build;

import net.quillcraft.build.autosave.SaveTask;
import net.quillcraft.build.manager.PluginManager;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class QuillCraftBuild extends JavaPlugin {

    private static QuillCraftBuild INSTANCE;

    public SaveTask saveTask;

    @Override
    public void onEnable(){
        INSTANCE = this;

        new SaveTask(this);

        new PluginManager(this);
    }

    @Nonnull
    public PluginCommand getCommand(@Nonnull String name){
        return Objects.requireNonNull(super.getCommand(name));
    }

    public static QuillCraftBuild getInstance(){
        return INSTANCE;
    }

}
