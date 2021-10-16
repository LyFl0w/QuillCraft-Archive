package net.quillcraft.build.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class FlightSpeed implements CommandExecutor {

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender,@Nonnull Command command,@Nonnull String label,@Nonnull String[] args){
        if(commandSender instanceof final Player player){
            if(args.length == 1){
                try{
                    player.setFlySpeed(args[0].isBlank() ? 0.1f : Float.parseFloat(args[0]));
                }catch(NumberFormatException exception){
                    player.sendMessage("§cPlease enter a valid number");
                    return false;
                }
            }
        }
        return false;
    }

}
