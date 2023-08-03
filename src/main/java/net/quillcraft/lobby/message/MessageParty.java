package net.quillcraft.lobby.message;

import com.google.common.io.ByteArrayDataInput;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.lobby.QuillCraftLobby;
import org.bukkit.entity.Player;

import java.util.logging.Level;


public class MessageParty extends Message {

    public MessageParty(QuillCraftLobby quillCraftLobby, byte[] data) {
        super(quillCraftLobby, data);
    }

    // TODO : CHANGE TARGET PLAYER BY PLAYERS IN THE PARTY

    @Override
    protected void onPluginMessageRepPlayer(Player player, String sub, ByteArrayDataInput in) {
        final String targetPlayerName = in.readUTF();

        if(sub.equalsIgnoreCase("Show")) {

            try {
                final Account account = new AccountProvider(player).getAccount();
                final Player targetPlayer = quillCraftLobby.getServer().getPlayer(targetPlayerName);

                if(targetPlayer == null) {
                    quillCraftLobby.getLogger().info("He is not connected to the server !");
                    return;
                }

                if(account.getVisibility() == Account.Visibility.PARTY) {
                    quillCraftLobby.getServer().getScheduler().runTaskLater(quillCraftLobby, () -> {
                        player.showPlayer(quillCraftLobby, targetPlayer);
                    }, 20L);
                }

                final Account targetAccount = new AccountProvider(targetPlayer).getAccount();
                if(targetAccount.getVisibility() == Account.Visibility.PARTY) {
                    quillCraftLobby.getServer().getScheduler().runTaskLater(quillCraftLobby, () -> {
                        targetPlayer.showPlayer(quillCraftLobby, player);
                    }, 20L);
                }

            } catch(AccountNotFoundException exception) {
                quillCraftLobby.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }

            return;
        }

        if(sub.equalsIgnoreCase("Hide")) {
            try {
                final Account account = new AccountProvider(player).getAccount();
                final Player targetPlayer = quillCraftLobby.getServer().getPlayer(targetPlayerName);

                if(targetPlayer == null) return;

                if(account.getVisibility() == Account.Visibility.PARTY)
                    quillCraftLobby.getServer().getScheduler().runTaskLater(quillCraftLobby, account::playVisibilityEffect, 20L);


                final Account targetAccount = new AccountProvider(targetPlayer).getAccount();
                if(targetAccount.getVisibility() == Account.Visibility.PARTY)
                    quillCraftLobby.getServer().getScheduler().runTaskLater(quillCraftLobby, targetAccount::playVisibilityEffect, 20L);


            } catch(AccountNotFoundException exception) {
                quillCraftLobby.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
        }

    }
}