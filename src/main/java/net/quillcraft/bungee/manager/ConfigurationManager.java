package net.quillcraft.bungee.manager;

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

    public Configuration getConfiguration() {
        return fileConfiguration;
    }

    @Nullable
    private static Configuration getFileConfiguration(String path) {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(path));
        } catch(IOException e) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }


}
