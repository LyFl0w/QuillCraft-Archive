package net.quillcraft.core.utils.builders;

import net.quillcraft.core.QuillCraftCore;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class YamlConfigurationBuilder {

    private final YamlConfiguration yamlConfiguration;
    private final File file;

    public YamlConfigurationBuilder(JavaPlugin javaPlugin, final String fileName, final boolean isDefault) {
        file = new File(javaPlugin.getDataFolder(), fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            javaPlugin.saveResource(fileName, isDefault);
        }
        yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            javaPlugin.getLogger().severe(exception.getMessage());
        }
    }

    public YamlConfigurationBuilder(JavaPlugin javaPlugin, String fileName) {
        file = new File(javaPlugin.getDataFolder(), fileName);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    throw new IOException("Unable to create file at specified path.");
                }
            }
        } catch (IOException exception) {
            javaPlugin.getLogger().severe(exception.getMessage());
        }
        yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            javaPlugin.getLogger().severe(exception.getMessage());
        }
    }

    public final FileConfiguration getConfig() {
        return yamlConfiguration;
    }

    public void save() {
        try {
            yamlConfiguration.save(file);
        } catch (IOException exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }
}