package net.quillcraft.bungee.manager.configuration;

import net.md_5.bungee.config.Configuration;

public enum ConfigurationBuilderManager {

    ;

    private final Configuration configuration;

    ConfigurationBuilderManager(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }


}
