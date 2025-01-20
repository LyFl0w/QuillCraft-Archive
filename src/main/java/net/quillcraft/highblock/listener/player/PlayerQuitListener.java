package net.quillcraft.highblock.listener.player;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.database.request.island.IslandRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PlayerQuitListener implements Listener {

    private static final HashMap<String, Integer> unloadWorlds = new HashMap<>();

    private final HighBlock highblock;

    public PlayerQuitListener(HighBlock highblock) {
        this.highblock = highblock;
    }

    public static Map<String, Integer> getUnloadWorlds() {
        return unloadWorlds;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final IslandRequest islandRequest = new IslandRequest(highblock.getDatabase(), false);

        try {
            if (!islandRequest.hasIsland(player.getUniqueId())) {
                highblock.getDatabase().closeConnection();
                return;
            }

            // UNLOAD ISLAND WORLD IF THERE IS NO PLAYER AFTER 5 MIN ( 20 L * 60 = 1200L (ticks) = 60 SEC = 1 MIN, so 1200L * 5 = 6000L (ticks) = 5 MIN)
            if (islandRequest.getMates(player.getUniqueId()).stream().parallel().filter(islandMate -> islandMate.player().isOnline()).count() <= 1) {
                final String worldName = islandRequest.getIslandWorldName(islandRequest.getIslandID(player.getUniqueId()));

                unloadWorlds.put(worldName, highblock.getServer().getScheduler().runTaskLater(highblock, () -> {
                    highblock.getServer().unloadWorld(worldName, true);
                    // AUTO REMOVE WORLD FROM unloadWorld LIST
                    unloadWorlds.remove(worldName);
                }, 6000L).getTaskId());
            }

            highblock.getDatabase().closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}