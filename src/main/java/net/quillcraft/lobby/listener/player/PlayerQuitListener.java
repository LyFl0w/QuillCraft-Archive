package net.quillcraft.lobby.listener.player;

import net.quillcraft.core.manager.LanguageManager;

import net.quillcraft.lobby.QuillCraftLobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.lumy.api.text.Text;

public class PlayerQuitListener implements Listener {

    private final QuillCraftLobby quillCraftLobby;

    public PlayerQuitListener(QuillCraftLobby quillCraftLobby) {
        this.quillCraftLobby = quillCraftLobby;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event){
        final Player player = event.getPlayer();

        Bukkit.getOnlinePlayers().stream().parallel().forEach(players ->
                players.sendMessage(LanguageManager.getLanguage(players).getMessage(Text.LOBBY_PLAYER_LEAVE).replace("%PLAYER%", player.getDisplayName())));

        quillCraftLobby.getScoreboardManager().removeScoreboard(player.getUniqueId());

        event.setQuitMessage("");
    }

}
