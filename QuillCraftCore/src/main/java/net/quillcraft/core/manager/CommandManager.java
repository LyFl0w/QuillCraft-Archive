package net.quillcraft.core.manager;

import net.quillcraft.core.QuillCraftCore;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CommandManager {

    private final List<String> commands;

    public CommandManager(Plugin plugin, File pluginFolder) {
        this.commands = new ArrayList<>(getCommandsOfPlugin(plugin, pluginFolder));
        if (!commands.contains("help")) commands.add("help");
    }

    private List<String> getCommandsOfPlugin(Plugin plugin, File pluginFolder) {
        try {
            final Map<String, Map<String, Object>> pluginLoaderCommands = plugin.getPluginLoader().getPluginDescription(pluginFolder).getCommands();
            final ArrayList<String> commandsToReturn = new ArrayList<>(pluginLoaderCommands.keySet());

            pluginLoaderCommands.values().stream().parallel().filter(stringObjectMap ->
                    stringObjectMap.containsKey("aliases")).forEach(stringObjectMap ->
                    commandsToReturn.addAll((Collection<String>) stringObjectMap.get("aliases")));
            return commandsToReturn;
        } catch (InvalidDescriptionException exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return new ArrayList<>();
    }

    public void registerCommands(Plugin plugin, File pluginFolder) {
        commands.addAll(getCommandsOfPlugin(plugin, pluginFolder));
    }

    public List<String> getCommands() {
        return commands;
    }
}
