package net.quillcraft.core.utils.builders;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class YamlConfigurationBuilder {

    private final YamlConfiguration yamlConfiguration;
    private final File file;

    public YamlConfigurationBuilder(JavaPlugin javaPlugin, final String fileName, final boolean isDefault){
        file = new File(javaPlugin.getDataFolder(), fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            javaPlugin.saveResource(fileName, isDefault);
        }
        yamlConfiguration = new YamlConfiguration();
        try{
            yamlConfiguration.load(file);
        }catch(IOException | InvalidConfigurationException exception){
            exception.printStackTrace();
        }
    }

    public final FileConfiguration getConfig(){
        return yamlConfiguration;
    }

    public void save(){
        try{
            yamlConfiguration.save(file);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}