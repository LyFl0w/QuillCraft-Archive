package net.quillcraft.build;

import net.quillcraft.build.manager.PluginManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class QuillCraftBuild extends JavaPlugin {

    @Override
    public void onEnable(){
        new PluginManager(this);
    }

    @Nonnull
    public PluginCommand getCommand(@Nonnull String name){
        return Objects.requireNonNull(super.getCommand(name));
    }

}
