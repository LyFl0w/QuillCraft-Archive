package net.lyflow.skyblock.event.island;

import net.lyflow.skyblock.SkyBlock;
import net.lyflow.skyblock.island.IslandDifficulty;
import net.lyflow.skyblock.database.request.island.IslandRequest;
import net.lyflow.skyblock.utils.ResourceUtils;

import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;

public class CreateIslandEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCancelled = false;

    public CreateIslandEvent(SkyBlock skyblock, Player player, IslandDifficulty islandDifficulty) {
        final IslandRequest islandRequest = new IslandRequest(skyblock.getDatabase(), false);
        try {
            if(islandRequest.hasIsland(player.getUniqueId())) {
                player.sendMessage("§cTu ne peux pas avoir plusieurs îles en même temps !");
                setCancelled(true);
                return;
            }

            player.sendMessage("§bCréation de votre île en cours §6§o(difficulté : "+islandDifficulty.name()+")");

            try {
                final String startPath = "skyblock-map/";
                final double x = -0.5;
                final double y = 100;
                final double z = 0.5;
                final float yaw = 90;
                final float pitch = 0;

                final int id = islandRequest.createIsland(player.getUniqueId(), islandDifficulty, startPath, x, y, z, yaw, pitch);

                // Make a copy of  Island World

                // create island in DB
                final String defaultPath = startPath+id;
                final File islandWorld = new File(skyBlock.getDataFolder(), "../../"+defaultPath);
                ResourceUtils.saveResourceFolder("maps/skyblock-"+islandDifficulty.name().toLowerCase(), islandWorld, skyBlock, false);

                // Load World
                skyBlock.getServer().createWorld(new WorldCreator(defaultPath));
                final Location spawn = new Location(skyBlock.getServer().getWorld(defaultPath), x, y, z, yaw, pitch);

                skyBlock.getDatabase().closeConnection();

                // Teleport to the world
                player.sendMessage("§bTéléportation en cours");
                player.teleport(spawn);
            } catch(SQLException e) {
                // DELETE USELESS WORLD FOLDER IF WE CAN'T GENERATE UTILS INFORMATION IN DATABASE
                throw new RuntimeException(e);
            }
        } catch(SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la base lors de la création d'une île", e);
        }
    }

    @Override @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean setCancelled) {
        this.isCancelled = setCancelled;
    }

}