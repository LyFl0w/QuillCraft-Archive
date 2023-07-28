package net.quillcraft.lobby.manager;

import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;
import net.quillcraft.lobby.QuillCraftLobby;
import org.bukkit.configuration.file.FileConfiguration;

public enum ConfigurationBuilderManager {

    NPC(new YamlConfigurationBuilder(QuillCraftLobby.getInstance(), "npc.yml", true)), HEAD(new YamlConfigurationBuilder(QuillCraftLobby.getInstance(), "head.yml", true)), MUGUET(new YamlConfigurationBuilder(QuillCraftLobby.getInstance(), "muguet.yml", true));

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
