package net.quillcraft.core.manager;

import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CommandManager{

    private final List<String> commands;

    public CommandManager(Plugin plugin, File pluginFolder){
        this.commands = new ArrayList<>(getCommandsOfPlugin(plugin, pluginFolder));
        if(!commands.contains("help")) commands.add("help");
    }

    private List<String> getCommandsOfPlugin(Plugin plugin, File pluginFolder){
        try{
            final Map<String, Map<String, Object>> commands = plugin.getPluginLoader().getPluginDescription(pluginFolder).getCommands();
            final ArrayList<String> commandsToReturn = new ArrayList<>(commands.keySet());

            commands.values().stream().parallel().forEach(stringObjectMap -> commandsToReturn.addAll((Collection<? extends String>) stringObjectMap.get("aliases")));
            return commandsToReturn;
        }catch(InvalidDescriptionException e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void registerCommands(Plugin plugin, File pluginFolder){
        commands.addAll(getCommandsOfPlugin(plugin, pluginFolder));
    }

    public List<String> getCommands(){
        return commands;
    }
}
