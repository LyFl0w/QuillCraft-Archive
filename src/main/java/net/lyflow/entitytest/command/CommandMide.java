package net.lyflow.entitytest.command;

import net.lyflow.entitytest.EntityTest;
import net.lyflow.entitytest.entity.Mask;
import net.lyflow.entitytest.reader.SchematicReader;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class CommandMide implements CommandExecutor {

    private final EntityTest entityTest;
    public CommandMide(EntityTest entityTest) {
        this.entityTest = entityTest;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(commandSender instanceof final Player player) {
            final Location location = player.getLocation();

            if(args[0].equalsIgnoreCase("create")) {
                if(entityTest.getMaskHashMap().containsKey(args[1])) {
                    player.sendMessage("§cMask id is already used");
                    return true;
                }
                try {
                    entityTest.getMaskHashMap().put(args[1], new Mask(location,
                            new SchematicReader(new FileInputStream(new File(entityTest.getDataFolder(), args[2]))),
                            new Vector(Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]))));
                } catch(IOException e) {
                    player.sendMessage("§cError schema location or vector");
                    throw new RuntimeException(e);
                }
                player.sendMessage("§bMask created");
                return true;
            }

            if(args[0].equalsIgnoreCase("move")) {
                if(!entityTest.getMaskHashMap().containsKey(args[1])) {
                    player.sendMessage("§cMask doesn't exist");
                    return true;
                }
                entityTest.getMaskHashMap().get(args[1]).applyVector();
                player.sendMessage("§bMask moved");
                return true;
            }

            player.sendMessage("§cMove or Create ?");
            return true;
        }
        return false;
    }
}
