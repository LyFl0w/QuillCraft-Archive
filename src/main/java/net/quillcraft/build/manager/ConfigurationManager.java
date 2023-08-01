package net.quillcraft.build.manager;

import net.quillcraft.build.QuillCraftBuild;
import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;

import org.bukkit.configuration.file.FileConfiguration;

public enum ConfigurationManager{

    WARPS(getConfiguration("warps.yml", true)),
    AUTO_SAVE(getConfiguration("auto_save.yml", true));

    private final YamlConfigurationBuilder yamlConfigurationBuilder;

    ConfigurationManager(YamlConfigurationBuilder yamlConfigurationBuilder){
        this.yamlConfigurationBuilder = yamlConfigurationBuilder;
    }

    public FileConfiguration getConfiguration(){
        return yamlConfigurationBuilder.getConfig();
    }

    public void saveFile(){
        yamlConfigurationBuilder.save();
    }

    private static YamlConfigurationBuilder getConfiguration(String fileName, boolean isDefault) {
        return new YamlConfigurationBuilder(QuillCraftBuild.getInstance(), fileName, isDefault);
    }

}
