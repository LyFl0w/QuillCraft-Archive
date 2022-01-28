package net.quillcraft.build;

import net.quillcraft.build.manager.PluginManager;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class QuillCraftBuild extends JavaPlugin {

    private static QuillCraftBuild INSTANCE;

    @Override
    public void onEnable(){
        INSTANCE = this;

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
