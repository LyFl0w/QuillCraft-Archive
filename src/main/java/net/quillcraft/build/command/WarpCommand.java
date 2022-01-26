package net.quillcraft.build.command;

import net.quillcraft.build.manager.ConfigurationManager;
import net.quillcraft.core.utils.CommandUtils;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter{

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(args.length == 2 && commandSender instanceof final Player player){
            final Location location = ConfigurationManager.WARPS.getConfiguration().getLocation(args[0]+"."+args[1]);
            if(location == null){
                player.sendMessage("§cThe selected warp does not exist !");
                return true;
            }
            player.teleport(location);
            player.sendMessage("§aYou have been teleported into the warp");
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
