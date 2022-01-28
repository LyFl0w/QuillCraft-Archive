package net.quillcraft.build.command;

import net.quillcraft.build.manager.ConfigurationManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class CreateWarpCommand implements CommandExecutor{

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

}
