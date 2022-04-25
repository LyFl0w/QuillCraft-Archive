package net.quillcraft.core.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerJumpEvent extends PlayerEvent implements Cancellable{

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCancelled = false;

    private final Location from;
    private final Location to;

    public PlayerJumpEvent(Player player, Location from, Location to){
        super(player);
        this.from = from;
        this.to = to;
    }

    public Location getFrom(){
        return from;
    }

    public Location getTo(){
        return to;
    }

    @Override
    public HandlerList getHandlers(){
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled(){
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean setCancelled){
        this.isCancelled = setCancelled;
    }
}
