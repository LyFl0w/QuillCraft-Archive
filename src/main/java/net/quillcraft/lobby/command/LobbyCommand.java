package net.quillcraft.lobby.command;

import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.lobby.location.LocationEnum;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lumy.api.text.Text;

import javax.annotation.Nonnull;

public class LobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@Nonnull CommandSender cmds, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if(cmds instanceof final Player player) {
            player.teleport(LocationEnum.LOBBY_SPAWN.getLocation());
            player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.COMMAND_LOBBY_SUCCESS));
            return true;
        }
        return false;
    }
}