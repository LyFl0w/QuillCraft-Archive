package net.lyflow.entitytest.listener.player;

import net.lyflow.entitytest.EntityTest;
import net.lyflow.entitytest.entity.CustomVehicle;
import net.lyflow.entitytest.entity.Mask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final EntityTest entityTest;
    public PlayerMoveListener(EntityTest entityTest) {
        this.entityTest = entityTest;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        /*
        final Player player = event.getPlayer();

        final CustomVehicle customVehicle = CustomVehicle.getCustomVehicle(player, entityTest);

        if(customVehicle == null) return;

        customVehicle.rotate(event.getPlayer().getLocation().getYaw());
         */
    }
}
