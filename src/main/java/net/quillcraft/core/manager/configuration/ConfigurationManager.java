package net.quillcraft.core.manager.configuration;

import net.quillcraft.core.QuillCraftCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public enum ConfigurationManager {

    DATA_ACCESS(getFileConfiguration(QuillCraftCore.getInstance().getDataManager().getDataAccessPath()));

    private final FileConfiguration fileConfiguration;

    ConfigurationManager(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    public FileConfiguration getConfiguration() {
        return fileConfiguration;
    }

    @Nullable
    private static FileConfiguration getFileConfiguration(String path) {
        try {
            final YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.load(new File(path));

            return yamlConfiguration;
        } catch(IOException|InvalidConfigurationException e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }

}
