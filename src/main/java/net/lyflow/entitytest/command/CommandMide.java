package net.lyflow.entitytest.command;

import net.lyflow.entitytest.EntityTest;
import net.lyflow.entitytest.entity.Mask;
import net.lyflow.entitytest.reader.SchematicReader;

import net.lyflow.entitytest.utils.CommandUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class CommandMide implements CommandExecutor, TabCompleter {

    private final static String[] argsCompletion = new String[]{"create", "move", "follow"};

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
                    entityTest.getMaskHashMap().put(args[1], new Mask(player, location,
                            new SchematicReader(new FileInputStream(new File(entityTest.getDataFolder(), args[2]))),
                            new Vector(Float.parseFloat(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]))));
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

            if(args[0].equalsIgnoreCase("follow")) {
                if(!entityTest.getMaskHashMap().containsKey(args[1])) {
                    player.sendMessage("§cMask doesn't exist");
                    return true;
                }

                player.sendMessage("§bMask" + ((entityTest.getMaskHashMap().get(args[1]).toggleFollow()) ? "follow" : "unfollow"));
                return true;
            }

            player.sendMessage("§cMove or Create ?");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if(commandSender instanceof Player) {
            if(args.length == 1) {
                if(args[0] == null) return null;
                return CommandUtils.completionTable(args[0], argsCompletion);
            }
        }
        return null;
    }
}
