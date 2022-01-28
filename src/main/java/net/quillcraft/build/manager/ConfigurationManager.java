package net.quillcraft.build.manager;

import net.quillcraft.build.QuillCraftBuild;

import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;

import org.bukkit.configuration.file.FileConfiguration;

public enum ConfigurationManager{

    WARPS(new YamlConfigurationBuilder(QuillCraftBuild.getInstance(), "warps.yml", true));

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

}
