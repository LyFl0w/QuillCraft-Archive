package net.quillcraft.core.listener.player;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.event.player.PlayerJumpEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PlayerMoveListener implements Listener {

    private static final double JUMP_VELOCITY = 0.42d;

    private final QuillCraftCore quillCraftCore;

    public PlayerMoveListener(QuillCraftCore quillCraftCore) {
        this.quillCraftCore = quillCraftCore;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final double velocityY = player.getVelocity().getY();

        if (velocityY > 0 && !isOnGround(player, player.getLocation().getBlock().getType()) && isJumping(velocityY)) {
            quillCraftCore.getServer().getPluginManager().callEvent(new PlayerJumpEvent(player, event.getFrom(), event.getTo()));
        }
    }

    private boolean isOnGround(Player player, Material blockMaterial) {
        return player.isOnGround() &&
                !(blockMaterial == Material.LADDER || blockMaterial == Material.SCAFFOLDING
                        || blockMaterial == Material.WATER || blockMaterial == Material.LAVA);
    }

    private boolean isJumping(double velocityY) {
        return Double.compare(BigDecimal.valueOf(velocityY).setScale(2, RoundingMode.HALF_UP).doubleValue(), JUMP_VELOCITY) == 0;
    }

}
