package net.quillcraft.lobby.command;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.manager.ConfigurationBuilderManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HeadCommand implements CommandExecutor {

    final BlockFace[] blockFaces = {BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST, BlockFace.EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.WEST_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST};
    private final QuillCraftLobby quillCraftLobby;

    public HeadCommand(QuillCraftLobby quillCraftLobby) {
        this.quillCraftLobby = quillCraftLobby;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if(commandSender instanceof final Player player) {
            if(args.length == 3) {
                try {
                    final int headYaw = Integer.parseInt(args[0]);
                    final int coins = Integer.parseInt(args[1]);
                    final String headID = args[2];
                    final HeadDatabaseAPI headDatabaseAPI = quillCraftLobby.getHeadDatabaseAPI();
                    if(!headDatabaseAPI.isHead(headID)) {
                        player.sendMessage("L'ID de la tête n'est pas reconnu");
                        return true;
                    }


                    if(headYaw > 15) return sendError(player);

                    final Block block = player.getWorld().getBlockAt(player.getLocation());
                    if(block.getType() == Material.AIR) {
                        final String worldName = player.getWorld().getName();
                        final StringBuilder pathBuilder = new StringBuilder(worldName);
                        final FileConfiguration headConfiguration = ConfigurationBuilderManager.HEAD.getConfiguration();
                        final ConfigurationSection configurationSection = headConfiguration.getConfigurationSection(worldName);

                        //Save head in config file
                        pathBuilder.append(".").append(configurationSection == null ? 0 : configurationSection.getKeys(false).size()).append(".");

                        headConfiguration.set(pathBuilder+"x", player.getLocation().getBlockX());
                        headConfiguration.set(pathBuilder+"y", player.getLocation().getBlockY());
                        headConfiguration.set(pathBuilder+"z", player.getLocation().getBlockZ());
                        headConfiguration.set(pathBuilder+"yaw", headYaw);
                        headConfiguration.set(pathBuilder+"coins", coins);
                        ConfigurationBuilderManager.HEAD.saveFile();

                        //Place head
                        block.setType(Material.PLAYER_HEAD);
                        headDatabaseAPI.setBlockSkin(block, headID);

                        //Rotation
                        final Skull thisSkull = (Skull) player.getWorld().getBlockAt(player.getLocation()).getState();
                        final Rotatable skullRotation = (Rotatable) thisSkull.getBlockData();
                        skullRotation.setRotation(blockFaces[headYaw]);
                        thisSkull.setBlockData(skullRotation);
                        thisSkull.update(true);

                        player.sendMessage("Votre tête a été créée");
                        return true;
                    }
                    player.sendMessage("Il y a un block la où vous vous trouver");
                    return true;

                } catch(Exception exception) {
                    return sendError(player);
                }
            }
        }
        return false;
    }

    private boolean sendError(Player player) {
        player.sendMessage("Sélectionner une rotation de tête valide (0-15)");
        return true;
    }

}
