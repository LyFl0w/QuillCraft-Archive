package net.quillcraft.core.manager.configuration;

import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;
import org.bukkit.configuration.file.FileConfiguration;

public enum ConfigurationBuilderManager {

    ;

    private final YamlConfigurationBuilder yamlConfigurationBuilder;

    ConfigurationBuilderManager(YamlConfigurationBuilder yamlConfigurationBuilder) {
        this.yamlConfigurationBuilder = yamlConfigurationBuilder;
    }

    public FileConfiguration getConfiguration() {
        return yamlConfigurationBuilder.getConfig();
    }

    public void saveFile() {
        yamlConfigurationBuilder.save();
    }

}
