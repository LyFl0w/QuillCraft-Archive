package net.quillcraft.lobby.command;

import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.lobby.location.LocationEnum;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lumy.api.text.Text;

import javax.annotation.Nonnull;

public class SetLobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@Nonnull CommandSender cmds, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if(cmds instanceof Player player) {
            final LanguageManager languageManager = LanguageManager.getLanguage(player);
            if(player.getWorld().getName().equalsIgnoreCase(Bukkit.getWorlds().get(0).getName())) {
                Location location = player.getLocation();
                LocationEnum.LOBBY_SPAWN.setLocation(location);
                player.sendMessage(languageManager.getMessage(Text.COMMAND_SETLOBBY_SUCCESS));
                return true;
            }
            player.sendMessage(languageManager.getMessage(Text.COMMAND_SETLOBBY_ERROR_WORLD));
            return true;
        }
        return false;
    }
}