package net.quillcraft.core.manager;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;
import org.bukkit.configuration.file.FileConfiguration;

public enum ConfigurationManager {

    DATA_ACCESS(new YamlConfigurationBuilder(QuillCraftCore.getInstance(), "data_access.yml", true));

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
