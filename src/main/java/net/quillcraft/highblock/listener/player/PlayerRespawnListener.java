package net.quillcraft.highblock.listener.player;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.database.request.island.IslandRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.sql.SQLException;
import java.util.logging.Level;

public class PlayerRespawnListener implements Listener {

    private final HighBlock highblock;

    public PlayerRespawnListener(HighBlock highblock) {
        this.highblock = highblock;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final IslandRequest islandRequest = new IslandRequest(highblock.getDatabase(), false);
        try {
            if (islandRequest.hasIsland(player.getUniqueId()))
                event.setRespawnLocation(islandRequest.getSpawnLocation(islandRequest.getIslandID(player.getUniqueId())));
            highblock.getDatabase().closeConnection();
        } catch (SQLException e) {
            highblock.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
