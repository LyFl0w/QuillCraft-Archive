package net.quillcraft.core.manager;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;
import org.bukkit.configuration.file.FileConfiguration;

public enum ConfigurationManager {

    DEFAULT_CONFIG(QuillCraftCore.getInstance().getConfig()),
    DATA_ACCESS(new YamlConfigurationBuilder(QuillCraftCore.getInstance(), "data_access.yml", true).getConfig());

    private final FileConfiguration configuration;
    private final QuillCraftCore main = QuillCraftCore.getInstance();

    ConfigurationManager(FileConfiguration configuration){
        this.configuration = configuration;
    }

    public FileConfiguration getConfiguration(){
        return configuration;
    }

}
