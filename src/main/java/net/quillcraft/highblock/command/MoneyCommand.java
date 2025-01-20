package net.quillcraft.highblock.command;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.database.request.account.AccountRequest;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.logging.Level;

public class MoneyCommand implements CommandExecutor {

    private final HighBlock highblock;

    public MoneyCommand(HighBlock highblock) {
        this.highblock = highblock;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof final Player player) {
            if (args.length == 0) {
                try {
                    player.sendMessage("§aVous avez " + new AccountRequest(highblock.getDatabase(), true).getMoney(player.getUniqueId()) + "$");
                } catch (SQLException e) {
                    highblock.getLogger().log(Level.SEVERE, e.getMessage(), e);
                }
                return true;
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("send")) {
                    if (args[1].equals(player.getName())) {
                        player.sendMessage("§cVous ne pouvez pas vous envoyer de l'argent");
                        return true;
                    }
                    try {
                        final float amount = Math.round(Float.parseFloat(args[2]) * 100.0) / 100.0f;
                        if (amount <= 0) {
                            player.sendMessage("§cVous ne pouvez pas envoyer moins de 0$");
                            return true;
                        }

                        final AccountRequest accountRequest = new AccountRequest(highblock.getDatabase(), false);
                        final float playerMoney = accountRequest.getMoney(player.getUniqueId());
                        if (playerMoney < amount) {
                            highblock.getDatabase().closeConnection();
                            player.sendMessage("§cVous n'avez pas " + amount + "$");
                            return true;
                        }

                        final OfflinePlayer offlinePlayer = accountRequest.getOfflinePlayerByName(args[1]);
                        if (offlinePlayer == null) {
                            highblock.getDatabase().closeConnection();
                            player.sendMessage("§cLe joueur " + args[1] + " n'existe pas");
                            return true;
                        }

                        accountRequest.setMoney(player.getUniqueId(), accountRequest.getMoney(player.getUniqueId()) - amount);
                        accountRequest.setMoney(offlinePlayer.getUniqueId(), accountRequest.getMoney(offlinePlayer.getUniqueId()) + amount);
                        if (offlinePlayer.isOnline())
                            offlinePlayer.getPlayer().sendMessage("§aVous avez reçu " + amount + " $ de " + player.getName());
                        player.sendMessage("§aVous avez envoyé " + amount + "$ à " + offlinePlayer.getName());
                        highblock.getDatabase().closeConnection();
                    } catch (SQLException e) {
                        highblock.getLogger().log(Level.SEVERE, e.getMessage(), e);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§c" + args[2] + " n'est pas un nombre valide");
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
