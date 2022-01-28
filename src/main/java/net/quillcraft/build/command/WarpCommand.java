package net.quillcraft.build.command;

import net.quillcraft.build.manager.ConfigurationManager;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class WarpCommand implements CommandExecutor{

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

}
