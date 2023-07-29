package net.quillcraft.bungee.manager.configuration;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.quillcraft.bungee.QuillCraftBungee;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public enum ConfigurationManager {

    DATA_ACCESS(getFileConfiguration(QuillCraftBungee.getInstance().getDataManager().getDataAccessPath()));

    private final Configuration fileConfiguration;

    ConfigurationManager(Configuration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    @Nullable
    private static Configuration getFileConfiguration(String path) {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(path));
        } catch(IOException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return null;
    }

    public Configuration getConfiguration() {
        return fileConfiguration;
    }


}
