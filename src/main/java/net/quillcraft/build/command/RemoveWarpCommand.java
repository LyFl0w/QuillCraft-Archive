package net.quillcraft.build.command;

import net.quillcraft.build.manager.ConfigurationManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class RemoveWarpCommand implements CommandExecutor{

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(args.length == 2 && commandSender instanceof final Player player){
            final ConfigurationManager configurationManager = ConfigurationManager.WARPS;
            final FileConfiguration fileConfiguration = configurationManager.getConfiguration();

            final String path = args[0].toUpperCase()+"."+args[1].toUpperCase();
            if(!fileConfiguration.contains(path)){
                player.sendMessage("§cThe selected warp does not exist !");
                return true;
            }
            fileConfiguration.set(path, null);

            if(fileConfiguration.getConfigurationSection(args[0].toUpperCase()).getKeys(false).isEmpty())
                fileConfiguration.set(args[0].toUpperCase(), null);

            configurationManager.saveFile();
            player.sendMessage("§aThe selected warp has been removed");
            return true;
        }
        return false;
    }

}
