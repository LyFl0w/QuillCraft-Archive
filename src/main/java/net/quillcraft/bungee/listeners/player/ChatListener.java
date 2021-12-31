package net.quillcraft.bungee.listeners.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.party.PartyProvider;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(ChatEvent event){
        if(event.getSender() instanceof final ProxiedPlayer proxiedPlayer){
            final String message = event.getMessage();
            if(message.startsWith("&")){
                try{
                    final AccountProvider accountProvider = new AccountProvider(proxiedPlayer);
                    final Account account = accountProvider.getAccount();

                    if(account.hasParty()){
                        final PartyProvider partyProvider = new PartyProvider(account);
                        final String name = proxiedPlayer.getName();
                        final String messageBase = "[§9Party§f] <"+name+"> ";

                        partyProvider.sendMessageToPlayers(partyProvider.getParty(), message.replaceFirst("&", messageBase));
                        event.setCancelled(true);
                    }
                }catch(AccountNotFoundException | PartyNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }

}
