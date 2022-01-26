package net.quillcraft.build.command;

import net.quillcraft.build.manager.ConfigurationManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class RemoveWarpCommand implements CommandExecutor{

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(args.length == 2 && commandSender instanceof final Player player){
            final String path = args[0].toUpperCase()+"."+args[1].toUpperCase();
            if(!ConfigurationManager.WARPS.getConfiguration().contains(path)){
                player.sendMessage("§cThe selected warp does not exist !");
                return true;
            }
            ConfigurationManager.WARPS.getConfiguration().set(path, null);
            ConfigurationManager.WARPS.saveFile();
            player.sendMessage("§aThe selected warp has been removed");
            return true;
        }
        return false;
    }

}
