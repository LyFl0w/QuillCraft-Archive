package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.core.event.player.PlayerJumpEvent;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.game.InGameStatus;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJumpListener implements Listener{

    private final ParkourPvP parkourPvP;

    public PlayerJumpListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event){
        final GameManager gameManager = parkourPvP.getGameManager();

        if(gameManager.getInGameStatus().actualInGameStatusIs(InGameStatus.JUMP))
            gameManager.getPlayersDataGame().get(event.getPlayer().getName()).addJump();
    }

}