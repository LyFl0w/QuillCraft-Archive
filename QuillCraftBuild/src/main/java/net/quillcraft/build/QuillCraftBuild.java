package net.quillcraft.build;

import net.quillcraft.build.autosave.SaveTask;
import net.quillcraft.build.manager.PluginManager;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class QuillCraftBuild extends JavaPlugin {

    private static QuillCraftBuild instance;

    private SaveTask saveTask;

    @Override
    public void onEnable(){
        instance = this;

        new SaveTask(this);

        new PluginManager(this);
    }

    @Override @Nonnull
    public PluginCommand getCommand(@Nonnull String name){
        return Objects.requireNonNull(super.getCommand(name));
    }

    public static QuillCraftBuild getInstance(){
        return instance;
    }

    public SaveTask getSaveTask() {
        return saveTask;
    }

    public void setSaveTask(SaveTask saveTask) {
        this.saveTask = saveTask;
    }
}
