package net.quillcraft.bungee.listeners.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.event.EventHandler;

import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.FriendNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.friend.FriendProvider;
import net.quillcraft.commons.party.PartyProvider;

import org.lumy.api.text.Text;
import org.redisson.api.RedissonClient;

public class PostLoginListener implements Listener {

    private final RedissonClient redissonClient;
    private final QuillCraftBungee quillCraftBungee;
    public PostLoginListener(QuillCraftBungee quillCraftBungee){
        this.quillCraftBungee = quillCraftBungee;
        this.redissonClient = RedisManager.WEB_API.getRedisAccess().getRedissonClient();
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event){
        final ProxiedPlayer player = event.getPlayer();
        final TaskScheduler taskScheduler = quillCraftBungee.getProxy().getScheduler();

        redissonClient.getAtomicLong("players.size").incrementAndGet();

        taskScheduler.runAsync(quillCraftBungee, () -> {
            try{
                final Account account = new AccountProvider(player).getAccount();

                if(account.hasParty()){
                    new PartyProvider(account).getParty().getOnlinePlayers().stream().filter(players -> !players.getUniqueId().equals(player.getUniqueId())).forEach(players ->
                            players.sendMessage(LanguageManager.getLanguage(players).getMessageComponentReplace(Text.PARTY_JOIN_SERVER, "%PLAYER%", player.getName())));
                }

            }catch(AccountNotFoundException | PartyNotFoundException exception){
                exception.printStackTrace();
            }
        });

        taskScheduler.runAsync(quillCraftBungee, () -> {
            try{
                new FriendProvider(player).getFriends().getOnlineFriends().stream().parallel().forEach(onlineFriend ->
                        onlineFriend.sendMessage(LanguageManager.getLanguage(onlineFriend).getMessageComponentReplace(Text.FRIEND_JOIN_SERVER, "%PLAYER%", player.getName())));
            }catch(FriendNotFoundException exception){
                exception.printStackTrace();
            }
        });
    }

}