package net.quillcraft.core.listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class PlayerCommandPreprocessListener implements Listener {

    private final List<String> commands;

    public PlayerCommandPreprocessListener(List<String> commands) {
        this.commands = commands;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        if (player.isOp()) return;

        final String message = event.getMessage().replaceFirst("/", "").split(" ")[0];
        if (message.equalsIgnoreCase("help")) {
            player.sendMessage("HELP COMMAND SENT");
            event.setCancelled(true);
            return;
        }

        if (!commands.contains(message)) {
            player.sendMessage("Unknown command. Type \"/help\" for help");
            event.setCancelled(true);
        }
    }

}
