package net.lyflow.entitytest;

import net.lyflow.entitytest.command.CommandMide;
import net.lyflow.entitytest.entity.Mask;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class EntityTest extends JavaPlugin {

    private final HashMap<String, Mask> maskHashMap = new HashMap<>();
    private static EntityTest INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        getCommand("mide").setExecutor(new CommandMide(this));
    }

    public HashMap<String, Mask> getMaskHashMap() {
        return maskHashMap;
    }

    public static EntityTest getInstance() {
        return INSTANCE;
    }
}
