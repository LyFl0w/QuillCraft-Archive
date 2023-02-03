package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.InGameStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    private final ParkourPvP parkourPvP;
    public PlayerTeleportListener(ParkourPvP parkourPvP) {
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        if(parkourPvP.getGameManager().getInGameStatus() == InGameStatus.PVP) {
            if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                if(parkourPvP.getGameManager().getFileConfiguration().getInt("pvp.get-max-y") <= event.getTo().getY()) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
