package net.quillcraft.lobby.command;

import net.quillcraft.lobby.manager.ConfigurationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class HeadCommand implements CommandExecutor {



    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(commandSender instanceof final Player player){
            if(args.length == 1){
                final Block block = player.getWorld().getBlockAt(player.getLocation());
                if(block.getType() == Material.AIR){
                    FileConfiguration headConfiguration = ConfigurationManager.HEAD.getConfiguration();
                    headConfiguration.set("world.0.x",player.getLocation().getBlockX());
                    headConfiguration.set("world.0.y",player.getLocation().getBlockY());
                    headConfiguration.set("world.0.z",player.getLocation().getBlockZ());
                    ConfigurationManager.HEAD.saveFile();

                    block.setType(Material.PLAYER_HEAD);
                    player.sendMessage("Votre tête a été créée");
                    return true;
                }
                player.sendMessage("Il y a un block la où vous vous trouver");
                return true;
            }
        }
        return false;
    }
}
