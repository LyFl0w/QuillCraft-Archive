package net.quillcraft.bungee.listeners.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.event.EventHandler;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.bungee.text.Text;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.FriendNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.friend.FriendProvider;
import net.quillcraft.commons.party.PartyProvider;

public class DisconnectListener implements Listener {

    private final QuillCraftBungee quillCraftBungee;
    public DisconnectListener(QuillCraftBungee quillCraftBungee){
        this.quillCraftBungee = quillCraftBungee;
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event){
        final ProxiedPlayer player = event.getPlayer();

        final TaskScheduler taskScheduler = quillCraftBungee.getProxy().getScheduler();

        taskScheduler.runAsync(quillCraftBungee, () ->  {
            final FriendProvider friendProvider = new FriendProvider(player);
            try{
                friendProvider.getFriends().getOnlineFriends().stream().parallel().forEach(onlineFriend ->
                        onlineFriend.sendMessage(LanguageManager.getLanguage(onlineFriend).getMessageComponentReplace(Text.FRIEND_LEFT_SERVER, "%PLAYER%", player.getName())));
            }catch(FriendNotFoundException exception){
                exception.printStackTrace();
            }
            friendProvider.expireRedis();
        });
        taskScheduler.runAsync(quillCraftBungee, () -> {
            final AccountProvider accountProvider = new AccountProvider(player);
            try{
                final Account account = accountProvider.getAccount();
                if(account.hasParty()){
                    final PartyProvider partyProvider = new PartyProvider(account);
                    partyProvider.sendMessageToPlayers(partyProvider.getParty(), Text.PARTY_LEFT_SERVER, "%PLAYER%", player.getName());
                    //Temps delete party redis
                    if(partyProvider.getParty().getOnlinePlayers().size()-1 <= 0){
                        partyProvider.expireRedis();
                    }
                }
            }catch(AccountNotFoundException | PartyNotFoundException exception){
                exception.printStackTrace();
            }
            accountProvider.expireRedis();
        });
    }

}
