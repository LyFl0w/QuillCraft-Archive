package net.lyflow.entitytest.command;

import net.lyflow.entitytest.EntityTest;
import net.lyflow.entitytest.entity.CustomVehicle;
import net.lyflow.entitytest.entity.Ship;
import net.lyflow.entitytest.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

public class CommandShip implements CommandExecutor, TabCompleter {

    private final static String[] argsCompletion = new String[]{"create", "remove", "ride"};

    private final EntityTest entityTest;
    public CommandShip(EntityTest entityTest) {
        this.entityTest = entityTest;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(commandSender instanceof final Player player) {
            if(args[0].equalsIgnoreCase("create")) {
                try {
                    CustomVehicle.addCustomVehicle(player, args[1], entityTest);

                } catch(IOException e) {
                    player.sendMessage("§cError schema location");
                    throw new RuntimeException(e);
                }
                player.sendMessage("§Ship created");
                return true;
            }

            if(args[0].equalsIgnoreCase("remove")) {
                CustomVehicle.removeCustomVehicle(player, entityTest);
                player.sendMessage("§bShip removed");
                return true;
            }

            if(args[0].equalsIgnoreCase("ride")) {
                final Ship entity = new Ship(player.getLocation());

                (((CraftPlayer)player).getHandle()).n(entity);

                player.sendMessage("§bShip ride");
                return true;
            }

            player.sendMessage("§Create or Remove or Ride ?");
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
