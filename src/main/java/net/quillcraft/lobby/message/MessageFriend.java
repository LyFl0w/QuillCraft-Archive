package net.quillcraft.lobby.message;

import com.google.common.io.ByteArrayDataInput;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.lobby.QuillCraftLobby;
import org.bukkit.entity.Player;

import java.util.logging.Level;


public class MessageFriend extends Message {

    public MessageFriend(QuillCraftLobby quillCraftLobby, byte[] data) {
        super(quillCraftLobby, data);
    }

    @Override
    protected void onPluginMessageRepPlayer(Player player, String sub, ByteArrayDataInput in) {
        final String targetPlayerName = in.readUTF();

        if(sub.equalsIgnoreCase("Show")) {

            try {
                final Player targetPlayer = quillCraftLobby.getServer().getPlayer(targetPlayerName);

                if(targetPlayer == null) return;

                final Account account = new AccountProvider(player).getAccount();

                if(account.getVisibility() == Account.Visibility.FRIENDS) {
                    quillCraftLobby.getServer().getScheduler().runTaskLater(quillCraftLobby, () -> {
                        player.showPlayer(quillCraftLobby, targetPlayer);
                    }, 20L);
                }

                final Account targetAccount = new AccountProvider(targetPlayer).getAccount();
                if(targetAccount.getVisibility() == Account.Visibility.FRIENDS) {
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
                final Player targetPlayer = quillCraftLobby.getServer().getPlayer(targetPlayerName);

                if(targetPlayer == null) return;

                final Account account = new AccountProvider(player).getAccount();

                if(account.getVisibility() == Account.Visibility.FRIENDS) {
                    quillCraftLobby.getServer().getScheduler().runTaskLater(quillCraftLobby, () -> {
                        player.hidePlayer(quillCraftLobby, targetPlayer);
                    }, 20L);
                }

                final Account targetAccount = new AccountProvider(targetPlayer).getAccount();
                if(targetAccount.getVisibility() == Account.Visibility.FRIENDS) {
                    quillCraftLobby.getServer().getScheduler().runTaskLater(quillCraftLobby, () -> {
                        targetPlayer.hidePlayer(quillCraftLobby, player);
                    }, 20L);
                }

            } catch(AccountNotFoundException exception) {
                quillCraftLobby.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
        }

    }
}