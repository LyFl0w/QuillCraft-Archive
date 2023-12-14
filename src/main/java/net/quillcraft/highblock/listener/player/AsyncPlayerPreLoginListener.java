package net.quillcraft.highblock.listener.player;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.database.request.island.IslandRequest;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class AsyncPlayerPreLoginListener implements Listener {

    private final HighBlock skyblock;

    public AsyncPlayerPreLoginListener(HighBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Bukkit.getScheduler().runTask(skyblock, () -> {
            final UUID playerUUID = event.getUniqueId();
            final IslandRequest islandRequest = new IslandRequest(skyblock.getDatabase(), false);
            try {
                // LOAD ISLAND WORLD IF IT'S NOT LOADED
                if(islandRequest.hasIsland(playerUUID)) {
                    final String worldName = islandRequest.getIslandWorldName(islandRequest.getIslandID(playerUUID));
                    final HashMap<String, Integer> unloadWorlds = PlayerQuitListener.getUnloadWorlds();
                    if(unloadWorlds.containsKey(worldName)) {
                        // REMOVE TASK WHO UNLOAD ISLAND WORLD
                        skyblock.getServer().getScheduler().cancelTask(unloadWorlds.get(worldName));
                        // REMOVE ISLAND WORLD OF UNLOAD WORLDS LIST
                        unloadWorlds.remove(worldName);

                        skyblock.getDatabase().closeConnection();
                        return;
                    }

                    if(skyblock.getServer().getWorld(worldName) == null){
                        skyblock.getServer().createWorld(new WorldCreator(worldName));
                        skyblock.getDatabase().closeConnection();
                        return;
                    }
                }
                skyblock.getDatabase().closeConnection();
            } catch(SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

}