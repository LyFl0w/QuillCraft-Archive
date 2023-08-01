package net.quillcraft.build.listener.player;

import net.quillcraft.build.QuillCraftBuild;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerTeleportListener implements Listener {

    private final QuillCraftBuild main;

    public PlayerTeleportListener(QuillCraftBuild main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        main.saveTask.addWorld(event.getPlayer().getWorld());
    }
}
