package net.lyflow.entitytest.utils;

import org.bukkit.Bukkit;

public class NMSUtils {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    private final String packageName;
    public NMSUtils(String packageName) {
        this.packageName = packageName;
    }

    public Class<?> getNMSVersionClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft."+packageName+"."+VERSION+"."+name);
    }

    public Class<?> getCraftBukkitVersionClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit."+VERSION+"."+name);
    }

    public Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft."+packageName+"."+name);
    }

}
