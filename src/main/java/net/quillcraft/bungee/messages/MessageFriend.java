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
import net.quillcraft.commons.exception.FriendNotFoundException;
import net.quillcraft.commons.friend.Friend;
import net.quillcraft.commons.friend.FriendProvider;


public class MessageFriend extends Message{

    public MessageFriend(ProxyServer proxy, PluginMessageEvent event){
        super(proxy, event);
    }

    @Override
    protected void onPluginMessageRepPlayer(ProxiedPlayer player, String sub, ByteArrayDataInput in){
        //TODO: 1 Finir le code - 2 Mettre les messages dans la langue du joueur
        try{
            final LanguageManager languageManager = LanguageManager.getLanguage(player);
            final FriendProvider friendProvider = new FriendProvider(player);
            final Friend friend = friendProvider.getFriends();

            if(sub.equalsIgnoreCase("List")){
                final StringBuilder message = new StringBuilder("Vos amis :");
                final String textOnline = "Online";
                final String textOffline = "Offline";

                friend.getOnlineFriends().forEach(friendPlayer -> message.append("\n[").append(textOnline).append("] ").append(friendPlayer.getName()));
                friend.getOfflineFriendsName().forEach(friendName -> message.append("\n[").append(textOffline).append("] ").append(friendName));

                player.sendMessage(new TextComponent(message.toString()));
                return;
            }

            String targetPlayerName = in.readUTF();
            final ProxiedPlayer targetPlayer = proxy.getPlayer(targetPlayerName);

            if(targetPlayer == null){
                player.sendMessage(new TextComponent("Le joueur "+targetPlayerName+" n'est pas connecté"));
                return;
            }

            targetPlayerName = targetPlayer.getName();


            if(sub.equalsIgnoreCase("Add")){

                if(friendProvider.hasRequested(targetPlayer)){
                    player.sendMessage(new TextComponent("Le joueur sélectionné a déjà recu un demande d'ami "));
                    return;
                }

                if(friend.getOnlineFriends().contains(targetPlayer)){
                    player.sendMessage(new TextComponent("Le joueur sélectionné est déjà dans vos amis"));
                    return;
                }

                friendProvider.sendAddRequest(targetPlayer, new AccountProvider(targetPlayer).getAccount());
                return;
            }

            if(sub.equalsIgnoreCase("Remove")){

                if(!friend.getOnlineFriends().contains(targetPlayer)){
                    player.sendMessage(new TextComponent("Le joueur sélectionné n'est pas dans vos mais"));
                    return;
                }

                //friendProvider.removeFriend(targetPlayer);

                return;
            }

            if(sub.equalsIgnoreCase("Accept")){

                final FriendProvider targetFriendProvider = new FriendProvider(targetPlayer);

                if(!targetFriendProvider.hasRequested(player)){
                    player.sendMessage(new TextComponent("Le joueur "+targetPlayerName+" ne vous a pas demandé en ami !"));
                    return;
                }

                if(friend.getFriendsName().contains(targetPlayer.getName())){
                    player.sendMessage(new TextComponent("Vous êtes déjà ami avec "+targetPlayerName));
                    return;
                }

                final Friend targetFriend = targetFriendProvider.getFriends();

                friend.addPlayer(targetPlayer);
                targetFriend.addPlayer(player);

                friendProvider.updateFriends(friend);
                targetFriendProvider.updateFriends(targetFriend);

                player.sendMessage(new TextComponent("Vous avez accepté la demande d'ami de "+targetPlayer.getName()));
                targetPlayer.sendMessage(new TextComponent(player.getName()+" a accepté votre demande d'ami"));
            }
        }catch(FriendNotFoundException | AccountNotFoundException e){
            e.printStackTrace();
        }
    }
}