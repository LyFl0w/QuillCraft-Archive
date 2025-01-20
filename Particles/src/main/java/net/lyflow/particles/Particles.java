package net.lyflow.particles;

import net.lyflow.particles.command.ParticleCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Particles extends JavaPlugin{

    @Override
    public void onEnable(){
        getCommand("particle").setExecutor(new ParticleCommand(this));
        getCommand("particle").setTabCompleter(new ParticleCommand(this));
    }


    @Override @Nonnull
    public PluginCommand getCommand(@Nonnull String name){
        return Objects.requireNonNull(super.getCommand(name));
    }
}
