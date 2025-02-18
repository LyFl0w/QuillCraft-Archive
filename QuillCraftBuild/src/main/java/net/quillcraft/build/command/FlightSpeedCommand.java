package net.quillcraft.build.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class FlightSpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(commandSender instanceof final Player player && args.length <= 1){
            try{
                final float speed = args.length == 0 || args[0].isBlank() ? 0.1f : Float.parseFloat(args[0])/10;
                if(speed > 1){
                    player.sendMessage("§c"+speed*10+" is too high to fly");
                    return true;
                }
                player.setFlySpeed(speed);
                player.sendMessage("§2Your flight speed is : §b"+speed*10);
                return true;
            }catch(NumberFormatException exception){
                player.sendMessage("§cPlease enter a valid number");
                return true;
            }
        }
        return false;
    }
}