package net.quillcraft.core.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;
import java.util.List;

public class PlayerCommandSendListener implements Listener {

    private final List<String> commands;

    public PlayerCommandSendListener(List<String> commands) {
        this.commands = commands;
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        final Collection<String> requestedCommands = event.getCommands();
        if (!event.getPlayer().isOp()) requestedCommands.clear();
        requestedCommands.addAll(commands);
    }

}
