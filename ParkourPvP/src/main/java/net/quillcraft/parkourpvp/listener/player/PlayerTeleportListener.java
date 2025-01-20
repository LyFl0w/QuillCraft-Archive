package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.InGameStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    private final ParkourPvP parkourPvP;
    private final int maxY;
    public PlayerTeleportListener(ParkourPvP parkourPvP) {
        this.parkourPvP = parkourPvP;
        this.maxY = parkourPvP.getGameManager().getFileConfiguration().getInt("pvp.get-max-y");
    }

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        if(parkourPvP.getGameManager().getInGameStatus() == InGameStatus.PVP && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if(maxY <= event.getTo().getY()) event.setCancelled(true);
        }
    }

}
