package net.quillcraft.build.command;

import net.quillcraft.build.manager.ConfigurationManager;
import net.quillcraft.core.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class CreateWarpCommand implements CommandExecutor, TabCompleter{

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(args.length == 2 && commandSender instanceof final Player player){
            final FileConfiguration fileConfiguration = ConfigurationManager.WARPS.getConfiguration();
            final String path = args[0].toUpperCase()+"."+args[1].toUpperCase();

            if(fileConfiguration.contains(path)){
                player.sendMessage("§bLe warp "+args[1].toUpperCase()+" a été mis à jour");
            }else{
                player.sendMessage("§aLe warp "+args[1].toUpperCase()+" a été créé dans la catégorie "+args[0].toUpperCase());
            }

            fileConfiguration.set(path, player.getLocation());

            ConfigurationManager.WARPS.saveFile();
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(commandSender instanceof Player){
            if(args.length == 1){
                if(args[0] == null) return null;
                return CommandUtils.completionTable(args[0], ConfigurationManager.WARPS.getConfiguration().getKeys(false).stream().toList());
            }
            if(args.length == 2){
                if(args[1] == null) return null;
                final ConfigurationSection configurationSection = ConfigurationManager.WARPS.getConfiguration().getConfigurationSection(args[0].toUpperCase());
                if(configurationSection == null) return null;

                return CommandUtils.completionTable(args[1], configurationSection.getKeys(false).stream().toList());
            }
        }
        return null;
    }

}
