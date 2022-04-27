package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.InGameStatus;
import net.quillcraft.parkourpvp.manager.GameManager;
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

        final GameManager gameManager = parkourPvP.getGameManager();
        final InGameStatus inGameStatus = gameManager.getInGameStatus();

        if(event.getHand() == EquipmentSlot.OFF_HAND){
            if(!inGameStatus.actualInGameStatusIs(InGameStatus.PVP)){
                final Player player = event.getPlayer();
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
        }

        if(inGameStatus.actualInGameStatusIs(InGameStatus.WAIT_LOBBY)){
            event.setCancelled(true);
            return;
        }

        final ItemStack itemStack = event.getItem();
        if(itemStack == null || itemStack.getType() == Material.AIR) return;

        // Interaction with respawn item
        if(inGameStatus.actualInGameStatusIs(InGameStatus.JUMP)){
            if(itemStack.getType() == Material.SLIME_BALL){
                final Player player = event.getPlayer();
                gameManager.getPlayersDataGame().get(player.getName()).addRespawn();
                player.teleport(gameManager.getCheckPoints().get(gameManager.getPlayersDataGame().get(player.getName()).getCheckPointID()).getLocation());
            }
        }
    }

}