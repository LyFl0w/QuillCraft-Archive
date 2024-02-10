package net.quillcraft.highblock.listener.player;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.database.request.island.IslandRequest;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class AsyncPlayerPreLoginListener implements Listener {

    private final HighBlock highblock;

    public AsyncPlayerPreLoginListener(HighBlock highblock) {
        this.highblock = highblock;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Bukkit.getScheduler().runTask(highblock, () -> {
            final UUID playerUUID = event.getUniqueId();
            final IslandRequest islandRequest = new IslandRequest(highblock.getDatabase(), false);
            try {
                // LOAD ISLAND WORLD IF IT'S NOT LOADED
                if (islandRequest.hasIsland(playerUUID)) {
                    final String worldName = islandRequest.getIslandWorldName(islandRequest.getIslandID(playerUUID));
                    final Map<String, Integer> unloadWorlds = PlayerQuitListener.getUnloadWorlds();
                    if (unloadWorlds.containsKey(worldName)) {
                        // REMOVE TASK WHO UNLOAD ISLAND WORLD
                        highblock.getServer().getScheduler().cancelTask(unloadWorlds.get(worldName));
                        // REMOVE ISLAND WORLD OF UNLOAD WORLDS LIST
                        unloadWorlds.remove(worldName);

                        highblock.getDatabase().closeConnection();
                        return;
                    }

                    if (highblock.getServer().getWorld(worldName) == null) {
                        highblock.getServer().createWorld(new WorldCreator(worldName));
                        highblock.getDatabase().closeConnection();
                        return;
                    }
                }
                highblock.getDatabase().closeConnection();
            } catch (SQLException e) {
                highblock.getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

}