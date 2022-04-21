package net.quillcraft.core.listener.player;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.event.player.PlayerJumpEvent;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener{

    private final static double jumpVelocity = 0.45d;

    private final QuillCraftCore quillCraftCore;
    public PlayerMoveListener(QuillCraftCore quillCraftCore){
        this.quillCraftCore = quillCraftCore;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        final Player player = event.getPlayer();
        final double velocityY = player.getVelocity().getY();

        if(velocityY > 0){
            final Material blockMaterial = player.getLocation().getBlock().getType();
            if(!player.isOnGround() && !(blockMaterial == Material.LADDER || blockMaterial == Material.SCAFFOLDING || blockMaterial == Material.WATER || blockMaterial == Material.LAVA)){
                if(Double.compare(velocityY, jumpVelocity) == 0) quillCraftCore.getServer().getPluginManager().callEvent(new PlayerJumpEvent(player, event.getFrom(), event.getTo()));
            }
        }
    }

}
