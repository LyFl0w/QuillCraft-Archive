package net.quillcraft.bungee.listeners.player;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
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

public record PostLoginListener(QuillCraftBungee quillCraftBungee) implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event){
        final ProxiedPlayer player = event.getPlayer();

        final TaskScheduler taskScheduler = quillCraftBungee.getProxy().getScheduler();


        taskScheduler.runAsync(quillCraftBungee, () -> {
            try{
                final Account account = new AccountProvider(player).getAccount();
                if(account.hasParty()){
                    new PartyProvider(account).getParty().getOnlinePlayers().stream().filter(players -> !players.getUniqueId().equals(player.getUniqueId())).forEach(players ->
                            players.sendMessage(new TextComponent(LanguageManager.getLanguage(players).getMessage(Text.PARTY_JOIN_SERVER)
                                    .replace("%PLAYER%", player.getName()))));
                }
            }catch(AccountNotFoundException | PartyNotFoundException e){
                e.printStackTrace();
            }
        });

        taskScheduler.runAsync(quillCraftBungee, () -> {
            try{
                new FriendProvider(player).getFriends().getOnlineFriends().stream().parallel().forEach(friends -> friends.sendMessage(new TextComponent("Le joueur "+player.getName()+" c'est connecté")));
            }catch(FriendNotFoundException e){
                e.printStackTrace();
            }
        });
    }

}