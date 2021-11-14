package net.quillcraft.lobby.command;

import net.quillcraft.lobby.manager.ConfigurationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;

public class HeadCommand implements CommandExecutor {

    final BlockFace[] blockFaces = {BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST,
            BlockFace.EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST, BlockFace.SOUTH,
            BlockFace.SOUTH_SOUTH_WEST, BlockFace.WEST_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST};

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if(commandSender instanceof final Player player){
            if(args.length == 2){
                try{
                    final int headYaw = Integer.parseInt(args[1]);
                    if(headYaw > 15) return sendError(player);

                    final Block block = player.getWorld().getBlockAt(player.getLocation());
                    if(block.getType() == Material.AIR){
                        final String worldName = player.getWorld().getName();
                        final StringBuilder pathBuilder = new StringBuilder(worldName);
                        final FileConfiguration headConfiguration = ConfigurationManager.HEAD.getConfiguration();
                        final ConfigurationSection configurationSection = headConfiguration.getConfigurationSection(worldName);

                        pathBuilder.append(".").append(configurationSection == null ? 0 : configurationSection.getKeys(false).size()).append(".");

                        headConfiguration.set(pathBuilder+"x", player.getLocation().getBlockX());
                        headConfiguration.set(pathBuilder+"y", player.getLocation().getBlockY());
                        headConfiguration.set(pathBuilder+"z", player.getLocation().getBlockZ());
                        headConfiguration.set(pathBuilder+"yaw", headYaw);
                        ConfigurationManager.HEAD.saveFile();

                        block.setType(Material.PLAYER_HEAD);

                        final Skull thisSkull = (Skull) player.getWorld().getBlockAt(player.getLocation()).getState();
                        final Rotatable skullRotation = (Rotatable) thisSkull.getBlockData();
                        skullRotation.setRotation(blockFaces[headYaw]);
                        thisSkull.setBlockData(skullRotation);
                        thisSkull.update(true);

                        player.sendMessage("Votre tête a été créée");
                        return true;
                    } player.sendMessage("Il y a un block la où vous vous trouver");
                    return true;
                }catch(Exception exception){
                    return sendError(player);
                }
            }
        }
        return false;
    }

    private boolean sendError(Player player){
        player.sendMessage("Sélectionner une rotation de tête valide (0-15)");
        return true;
    }
}
