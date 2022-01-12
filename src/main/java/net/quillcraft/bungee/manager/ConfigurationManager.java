package net.quillcraft.bungee.manager;

import net.md_5.bungee.config.Configuration;
import net.quillcraft.bungee.utils.builder.YamlConfigurationBuilder;

public enum ConfigurationManager {

    DATA_ACCESS(new YamlConfigurationBuilder("data_access.yml", true).getConfig());

    private final Configuration configuration;

    ConfigurationManager(Configuration configuration){
        this.configuration = configuration;
    }

    public Configuration getConfiguration(){
        return configuration;
    }


}
