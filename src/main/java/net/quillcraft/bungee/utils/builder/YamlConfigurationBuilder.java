package net.quillcraft.bungee.utils.builder;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.quillcraft.bungee.serialization.QuillCraftBungee;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;

public class YamlConfigurationBuilder {

    private final File file;
    private final ClassLoader classLoader;
    private String parentPath = "";
    private File dataFolder;
    private Configuration config;

    public YamlConfigurationBuilder(final String fileName, final boolean isDefault) {
        this(fileName, null, isDefault);
    }

    public YamlConfigurationBuilder(final String fileName, final String parentPath, final boolean isDefault) {
        QuillCraftBungee quillCraftBungee = QuillCraftBungee.getInstance();
        classLoader = quillCraftBungee.getClass().getClassLoader();
        dataFolder = quillCraftBungee.getDataFolder();

        if(parentPath != null) this.parentPath = parentPath;
        if(!this.parentPath.isEmpty()) dataFolder = new File(dataFolder, parentPath);

        if(!dataFolder.exists()) dataFolder.mkdir();

        file = new File(dataFolder, fileName);

        if(!file.exists()) {
            if(isDefault) {
                saveDefaultConfig();
                return;
            }
            try {
                if (!file.createNewFile()) throw new IllegalCallerException(fileName + " can't be created");
            } catch(IOException exception) {
                QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
        }
    }

    private YamlConfigurationBuilder saveDefaultConfig() {
        if(!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(classLoader.getResourceAsStream(parentPath.isEmpty() ? file.getName() : parentPath+"/"+file.getName())), file.toPath());
            } catch(IOException exception) {
                QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
            return this;
        }
        return this;
    }

    public Configuration getConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(dataFolder, file.getName()));
            return config;
        } catch(IOException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return null;
    }

    public void saveFile() {
        if(config == null) return;
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(dataFolder, file.getName()));
            config = null;
        } catch(IOException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

}