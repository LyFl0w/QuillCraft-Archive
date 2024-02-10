package net.quillcraft.highblock.event.island;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.database.request.island.IslandRequest;
import net.quillcraft.highblock.island.IslandDifficulty;
import net.quillcraft.highblock.utils.ResourceUtils;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;

public class CreateIslandEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCancelled = false;

    public CreateIslandEvent(HighBlock highblock, Player player, IslandDifficulty islandDifficulty) {
        final IslandRequest islandRequest = new IslandRequest(highblock.getDatabase(), false);
        try {
            if (islandRequest.hasIsland(player.getUniqueId())) {
                player.sendMessage("§cTu ne peux pas avoir plusieurs îles en même temps !");
                setCancelled(true);
                return;
            }

            player.sendMessage("§bCréation de votre île en cours §6§o(difficulté : " + islandDifficulty.getName() + ")");

            try {
                final String startPath = "highblock-map/";
                final double x = -0.5;
                final double y = 100;
                final double z = 0.5;
                final float yaw = 90;
                final float pitch = 0;

                final int id = islandRequest.createIsland(player.getUniqueId(), islandDifficulty, startPath, x, y, z, yaw, pitch);

                // Make a copy of  Island World

                // create island in DB
                final String defaultPath = startPath + id;
                final File islandWorld = new File(highblock.getDataFolder(), "../../" + defaultPath);
                ResourceUtils.saveResourceFolder("maps/highblock-" + islandDifficulty.name().toLowerCase(), islandWorld, highblock, false);

                // Load World
                highblock.getServer().createWorld(new WorldCreator(defaultPath));
                final Location spawn = new Location(highblock.getServer().getWorld(defaultPath), x, y, z, yaw, pitch);

                highblock.getDatabase().closeConnection();

                // Teleport to the world
                player.sendMessage("§bTéléportation en cours");
                player.teleport(spawn);
            } catch (SQLException e) {
                highblock.getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (SQLException e) {
            highblock.getLogger().log(Level.SEVERE, "Erreur lors de la récupération de la base lors de la création d'une île", e);
        }
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return getHandlerList();
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