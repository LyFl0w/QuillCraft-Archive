package net.quillcraft.bungee.messages;

import com.google.common.io.ByteArrayDataInput;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.bungee.text.Text;
import net.quillcraft.bungee.utils.StringUtils;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.party.Party;
import net.quillcraft.commons.party.PartyProvider;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

public class MessageParty extends Message{

    public MessageParty(ProxyServer proxy, PluginMessageEvent event){
        super(proxy, event);
    }

    @Override
    protected void onPluginMessageRepPlayer(ProxiedPlayer player, String sub, ByteArrayDataInput in){
        try{
            final AccountProvider accountProvider = new AccountProvider(player);
            final Account account = accountProvider.getAccount();

            final PartyProvider partyProvider = new PartyProvider(account);

            final LanguageManager languageManager = LanguageManager.getLanguage(account);

            if(sub.equals("SetOwner")){
                final Party party = partyProvider.getParty();
                if(!party.getOwnerUUID().equals(player.getUniqueId())){
                    player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_NOT_OWNER)));
                    return;
                }

                final String targetName = in.readUTF();
                if(player.getName().equalsIgnoreCase(targetName)){
                    player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_OWN_YOUR_SELF)));
                    return;
                }
                if(!StringUtils.containsIgnoreCase(party.getFollowersNames(), targetName)){
                    player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PLAYER_NOT_IN_YOUR_PARTY).replace("%PLAYER%", targetName)));
                    return;
                }
                final UUID targetUUID = party.getUUIDByNameInFollowersList(targetName);

                party.addPlayer(player);
                party.setOwner(targetUUID);
                partyProvider.updateParty(party);

                final ProxiedPlayer proxiedPlayer = proxy.getPlayer(targetUUID);
                if(proxiedPlayer != null)
                    proxiedPlayer.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PROMOTE_OWNER)));
                player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_UNPROMOTE_OWNER)));
                return;
            }

            if(sub.equals("Kick")){
                final Party party = partyProvider.getParty();
                final String targetName = in.readUTF();
                if(!StringUtils.containsIgnoreCase(party.getFollowersNames(), targetName)){
                    player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PLAYER_NOT_IN_YOUR_PARTY)));
                    return;
                }
                final UUID targetUUID = party.getUUIDByNameInFollowersList(targetName);
                final AccountProvider targetAccountProvider = new AccountProvider(targetUUID);
                final Account targetAccount = targetAccountProvider.getAccount();

                targetAccount.setPartyUUID(null);
                targetAccountProvider.updateAccount(targetAccount);

                party.removePlayer(targetName);
                partyProvider.updateParty(party);

                final ProxiedPlayer proxiedPlayer = proxy.getPlayer(targetUUID);
                if(proxiedPlayer != null)
                    proxiedPlayer.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PERSO_KICK)));
                partyProvider.sendMessageToPlayers(party, Text.PARTY_GENERAL_KICK, "%PLAYER%", targetName);
                return;
            }

            if(sub.equals("Leave")){
                final Party party = partyProvider.getParty();
                List<UUID> players = party.getOnlineFollowersUUID();

                account.setPartyUUID(null);
                accountProvider.updateAccount(account);

                if(!party.getOwnerUUID().equals(player.getUniqueId())){
                    party.removePlayer(player);
                    partyProvider.updateParty(party);
                    player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PERSO_LEAVE)));
                    partyProvider.sendMessageToPlayers(party, Text.PARTY_GENERAL_LEAVE, "%PLAYER%", player.getName());
                    return;
                }

                if(players.isEmpty()){
                    final List<UUID> offlineFollowersUUID = party.getOfflineFollowersUUID();
                    if(offlineFollowersUUID.isEmpty()){
                        partyProvider.deleteParty(party);
                        player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PERSO_LEAVE)));
                        return;
                    }
                    players = offlineFollowersUUID;
                    partyProvider.deletePartyFromRedis();
                }

                final int randomNumber = new SecureRandom().nextInt(players.size());

                final UUID newOwnerUUID = players.get(randomNumber);
                final ProxiedPlayer newOwner = proxy.getPlayer(newOwnerUUID);

                party.setOwner(newOwnerUUID);
                partyProvider.updateParty(party);

                if(newOwner != null)
                    newOwner.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PROMOTE_OWNER)));

                player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PERSO_LEAVE)));

                partyProvider.sendMessageToPlayers(party, Text.PARTY_GENERAL_LEAVE, "%PLAYER%", player.getName());
                return;
            }

            if(sub.equals("Delete")){
                final Party party = partyProvider.getParty();
                if(!party.getOwnerUUID().equals(player.getUniqueId())){
                    player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_NOT_OWNER)));
                    return;
                }

                partyProvider.deleteParty(party);
                partyProvider.sendMessageToPlayers(party, Text.PARTY_DELETE);
                return;
            }

            if(sub.equals("List")){
                final Party party = partyProvider.getParty();
                final ProxiedPlayer owner = proxy.getPlayer(party.getOwnerUUID());

                final String textOnline = languageManager.getMessage(Text.PARTY_PLAYERS_ONLINE);
                final String textOffline = languageManager.getMessage(Text.PARTY_PLAYERS_OFFLINE);

                final StringBuilder ownerPartMessage = new StringBuilder("§f\n[")
                        .append(languageManager.getMessage(Text.PARTY_PLAYERS_OWNER))
                        .append("\\").append((owner == null) ? textOffline : textOnline).append("] §b")
                        .append(party.getOwnerName()).append("§f");

                final StringBuilder messageBuilder = new StringBuilder();

                party.getOnlineFollowersUUID().forEach(uuid -> {
                    final ProxiedPlayer proxiedPlayer = proxy.getPlayer(uuid);
                    messageBuilder.append("\n[").append(textOnline).append("] ")
                            .append("§b").append(proxiedPlayer.getName()).append("§f");
                });

                party.getOfflineFollowersName().forEach(name -> messageBuilder.append("\n[")
                        .append(textOffline).append("] §b").append(name).append("§f"));

                player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PLAYERS_DEFAULT_MESSAGE)+"\n"
                        + languageManager.getMessage(Text.PARTY_PLAYERS_MEMBERS_MESSAGE).replace("%NUMBER%", Integer.toString(party.getPlayers().size()))
                        + ((owner == null) ? messageBuilder + ownerPartMessage.toString() : ownerPartMessage + messageBuilder.toString())));
                return;
             }

            if(sub.equals("Accept")){
                try{
                    final String targetName = in.readUTF();
                    final ProxiedPlayer targetPlayer = proxy.getPlayer(targetName);

                    if(targetPlayer == null){
                        player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PLAYER_IS_OFFLINE).replace("%PLAYER%", targetName)));
                        return;
                    }

                    if(account.hasParty()){
                        player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PLAYER_ARE_ALREADY_IN_PARTY)));
                        return;
                    }

                    final AccountProvider targetAccountProvider = new AccountProvider(targetPlayer);
                    final Account targetAccount = targetAccountProvider.getAccount();

                    if(!targetAccount.hasParty()){
                        player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PLAYER_IS_OFFLINE).replace("%PLAYER%", targetPlayer.getDisplayName())));
                        return;
                    }

                    final PartyProvider partyProviderTarget = new PartyProvider(targetAccount);
                    if(!partyProviderTarget.hasInvited(player)){
                        player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_INVITATION_EXPIRED)));
                        return;
                    }

                    final Party partyTarget = partyProviderTarget.getParty();
                    partyProviderTarget.sendMessageToPlayers(partyTarget, Text.PARTY_GENERAL_JOIN, "%PLAYER%", player.getDisplayName());
                    partyTarget.addPlayer(player);
                    partyProviderTarget.updateParty(partyTarget);

                    account.setPartyUUID(partyTarget.getPartyUUID());
                    accountProvider.updateAccount(account);
                    player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PERSO_JOIN).replace("%PLAYER%", targetPlayer.getDisplayName())));
                }catch(AccountNotFoundException e){
                    e.printStackTrace();
                }
                return;
            }

            if(sub.equals("Invite")){
                final String targetName = in.readUTF();
                final ProxiedPlayer targetPlayer = proxy.getPlayer(targetName);

                if(targetPlayer == null){
                    player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PLAYER_IS_OFFLINE).replace("%PLAYER%", targetName)));
                    return;
                }
                final AccountProvider targetAccountProvider = new AccountProvider(targetPlayer);
                final Account targetAccount = targetAccountProvider.getAccount();


                if(targetAccount.hasParty()){
                    if(targetAccount.getPartyUUID().equals(account.getPartyUUID())){
                        player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PLAYER_IS_ALREADY_IN_YOUR_PARTY).replace("%PLAYER%", targetPlayer.getDisplayName())));
                        return;
                    }
                    player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_PLAYER_IS_ALREADY_IN_ANOTHER_PARTY).replace("%PLAYER%", targetPlayer.getDisplayName())));
                    return;
                }

                if(!account.hasParty()){
                    final Party party = partyProvider.createParty();
                    account.setPartyUUID(party.getPartyUUID());
                    accountProvider.updateAccount(account);
                }

                if(partyProvider.sendInviteRequest(targetPlayer, targetAccount)){
                    player.sendMessage(new TextComponent(languageManager.getMessage(Text.PARTY_INVITATION_SEND).replace("%PLAYER%", targetPlayer.getDisplayName())));
                }else{
                    //TODO : METTRE EN ANGLAIS
                    player.sendMessage(new TextComponent("Vous avez déjà invité "+targetPlayer.getName()+" dans votre party !"));
                }
            }
        }catch(PartyNotFoundException | AccountNotFoundException e){
            e.printStackTrace();
        }
    }
}