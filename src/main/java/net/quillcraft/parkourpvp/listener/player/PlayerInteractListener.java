package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.ParkourPvP;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerInteractListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onPLayerInteractEvent(PlayerInteractEvent event){
        if(event.getHand() == EquipmentSlot.OFF_HAND) return;

        final ItemStack itemStack = event.getItem();
        if(itemStack == null || itemStack.getType() == Material.AIR) return;

        final Player player = event.getPlayer();
        final GameManager gameManager = parkourPvP.getGameManager();

        switch(gameManager.getInGameStatus()){
            // Interaction with respawn item
            case JUMP -> {
                if(itemStack.getType() == Material.SLIME_BALL){
                    player.teleport(gameManager.getCheckPoints().get(gameManager.getPlayersData().get(player.getName()).getCheckPointID()).getLocation());
                }
            }
        }

    }
}
