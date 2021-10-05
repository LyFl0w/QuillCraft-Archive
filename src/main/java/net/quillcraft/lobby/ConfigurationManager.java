package net.quillcraft.lobby;

import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;
import org.bukkit.configuration.file.FileConfiguration;

public enum ConfigurationManager {

    NPC(new YamlConfigurationBuilder(QuillCraftLobby.getInstance(), "npc.yml", true));

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
