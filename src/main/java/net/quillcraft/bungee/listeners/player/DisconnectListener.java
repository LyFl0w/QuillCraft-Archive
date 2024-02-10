package net.quillcraft.bungee.listeners.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.event.EventHandler;
import net.quillcraft.bungee.serialization.QuillCraftBungee;
import net.quillcraft.bungee.data.redis.RedisManager;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.FriendNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.friend.FriendProvider;
import net.quillcraft.commons.game.GameEnum;
import net.quillcraft.commons.party.Party;
import net.quillcraft.commons.party.PartyProvider;
import net.quillcraft.lumy.api.text.Text;
import org.redisson.api.RedissonClient;

import java.util.logging.Level;

public class DisconnectListener implements Listener {

    private final RedissonClient redissonClient;
    private final QuillCraftBungee quillCraftBungee;

    public DisconnectListener(QuillCraftBungee quillCraftBungee) {
        this.quillCraftBungee = quillCraftBungee;
        this.redissonClient = RedisManager.WEB_API.getRedisAccess().getRedissonClient();
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final TaskScheduler taskScheduler = quillCraftBungee.getProxy().getScheduler();

        redissonClient.getAtomicLong("players.size").set(quillCraftBungee.getProxy().getOnlineCount()-1L);
        redissonClient.getTopic("players.size.update").publish(0);

        taskScheduler.runAsync(quillCraftBungee, () -> {
            GameEnum.removePlayerWaiting(player.getUniqueId());

            final AccountProvider accountProvider = new AccountProvider(player);
            try {
                final Account account = accountProvider.getAccount();
                if(account.hasParty()) {
                    final PartyProvider partyProvider = new PartyProvider(account);
                    final Party party = partyProvider.getParty();
                    partyProvider.sendMessageToPlayers(party, Text.PARTY_LEFT_SERVER, "%PLAYER%", player.getName());
                    //Temps delete party redis
                    if(party.getOnlinePlayers().size()-1 <= 0) partyProvider.expireRedis();
                }
            } catch(AccountNotFoundException|PartyNotFoundException exception) {
                QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
            accountProvider.expireRedis();

            final FriendProvider friendProvider = new FriendProvider(player);
            try {
                friendProvider.getFriends().getOnlineFriends().stream().parallel().forEach(onlineFriend -> onlineFriend.sendMessage(LanguageManager.getLanguage(onlineFriend).getMessageComponentReplace(Text.FRIEND_LEFT_SERVER, "%PLAYER%", player.getName())));
            } catch(FriendNotFoundException exception) {
                QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
            friendProvider.expireRedis();

        });

    }

}