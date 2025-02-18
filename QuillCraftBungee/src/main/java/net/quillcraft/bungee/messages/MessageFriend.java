package net.quillcraft.bungee.messages;

import com.google.common.io.ByteArrayDataInput;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.bungee.utils.StringUtils;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.FriendNotFoundException;
import net.quillcraft.commons.friend.Friend;
import net.quillcraft.commons.friend.FriendProvider;
import net.quillcraft.lumy.api.text.Text;

import java.util.UUID;
import java.util.logging.Level;


public class MessageFriend extends Message {

    public MessageFriend(ProxyServer proxy, PluginMessageEvent event) {
        super(proxy, event);
    }

    @Override
    protected void onPluginMessageRepPlayer(ProxiedPlayer player, String sub, ByteArrayDataInput in) {
        proxy.getScheduler().runAsync(QuillCraftBungee.getInstance(), () -> {
            try {
                final LanguageManager languageManager = LanguageManager.getLanguage(player);
                final FriendProvider friendProvider = new FriendProvider(player);
                final Friend friend = friendProvider.getFriends();

                if(sub.equalsIgnoreCase("List")) {
                    if(friend.getFriendsUUID().isEmpty()) {
                        player.sendMessage(languageManager.getMessageComponent(Text.FRIEND_NO_FRIENDS));
                        return;
                    }

                    final StringBuilder message = new StringBuilder(languageManager.getMessage(Text.FRIEND_FRIENDS_LIST).replace("%COUNT%", Integer.toString(friend.getFriendsUUID().size())));
                    final String textOnline = languageManager.getMessage(Text.STATUS_PLAYER_ONLINE);
                    final String textOffline = languageManager.getMessage(Text.STATUS_PLAYER_OFFLINE);

                    friend.getOnlineFriends().forEach(friendPlayer -> message.append("\n[").append(textOnline).append("] ").append(friendPlayer.getName()));
                    friend.getOfflineFriendsName().forEach(friendName -> message.append("\n[").append(textOffline).append("] ").append(friendName));

                    player.sendMessage(new TextComponent(message.toString()));
                    return;
                }

                final String tempTargetName = in.readUTF();
                final ProxiedPlayer targetPlayer = proxy.getPlayer(tempTargetName);

                if(sub.equalsIgnoreCase("Remove")) {
                    if(!StringUtils.containsIgnoreCase(friend.getFriendsName(), tempTargetName)) {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_PLAYER_IS_NOT_YOUR_FRIEND, PLAYER_PATTER, tempTargetName));
                        return;
                    }

                    final UUID targetUUID = friend.getUUIDByFriendName(tempTargetName);

                    //Target UUID is not null because we check before (ligne 59) if the target player is in his friends

                    final FriendProvider targetFriendProvider = new FriendProvider(targetUUID);
                    final Friend targetFriend = targetFriendProvider.getFriends();

                    final String targetName = targetPlayer == null ? friend.getNameByFriendUUID(targetUUID) : targetPlayer.getName();

                    friend.removePlayer(targetUUID);
                    targetFriend.removePlayer(player.getUniqueId());

                    friendProvider.updateFriends(friend);
                    targetFriendProvider.updateFriends(targetFriend);

                    if(targetPlayer != null)
                        targetPlayer.sendMessage(LanguageManager.getLanguage(targetPlayer).getMessageComponentReplace(Text.FRIEND_PLAYER_DELETED_FROM_HIS_FRIEND, PLAYER_PATTER, player.getName()));

                    player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_DELETED, PLAYER_PATTER, targetName));
                    return;
                }

                if(sub.equalsIgnoreCase("Add")) {
                    if(targetPlayer == null) {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_PLAYER_IS_OFFLINE, PLAYER_PATTER, tempTargetName));
                        return;
                    }

                    final String targetName = targetPlayer.getName();

                    if(friendProvider.hasRequested(targetPlayer)) {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_PLAYER_ALREADY_RECEIVED_REQUEST, PLAYER_PATTER, targetName));
                        return;
                    }

                    if(friend.getOnlineFriends().contains(targetPlayer)) {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_PLAYER_IS_ALREADY_YOUR_FRIEND, PLAYER_PATTER, targetName));
                        return;
                    }

                    if(friendProvider.sendAddRequest(targetPlayer, new AccountProvider(targetPlayer).getAccount())) {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_SEND_REQUEST, PLAYER_PATTER, targetName));
                    } else {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_PLAYER_ALREADY_RECEIVED_REQUEST, PLAYER_PATTER, targetName));
                    }
                    return;
                }

                if(sub.equalsIgnoreCase("Accept")) {
                    if(targetPlayer == null) {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_PLAYER_IS_OFFLINE, PLAYER_PATTER, tempTargetName));
                        return;
                    }

                    final FriendProvider targetFriendProvider = new FriendProvider(targetPlayer);

                    if(!targetFriendProvider.hasRequested(player)) {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_NO_RECEIVED_REQUEST, PLAYER_PATTER, targetPlayer.getName()));
                        return;
                    }

                    final String targetName = targetPlayer.getName();

                    if(friend.getFriendsName().contains(targetName)) {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_PLAYER_IS_ALREADY_YOUR_FRIEND, PLAYER_PATTER, targetName));
                        return;
                    }

                    final Friend targetFriend = targetFriendProvider.getFriends();

                    friend.addPlayer(targetPlayer);
                    targetFriend.addPlayer(player);

                    friendProvider.updateFriends(friend);
                    targetFriendProvider.updateFriends(targetFriend);

                    player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_PLAYER_ACCEPT_REQUEST, PLAYER_PATTER, targetName));
                    targetPlayer.sendMessage(LanguageManager.getLanguage(targetPlayer).getMessageComponentReplace(Text.FRIEND_PLAYER_ACCEPT_YOUR_REQUEST, PLAYER_PATTER, player.getName()));
                    return;
                }

                if(sub.equalsIgnoreCase("Deny")) {
                    if(targetPlayer == null) {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_PLAYER_IS_OFFLINE, PLAYER_PATTER, tempTargetName));
                        return;
                    }

                    final FriendProvider targetFriendProvider = new FriendProvider(targetPlayer);

                    if(!targetFriendProvider.hasRequested(player)) {
                        player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_NO_RECEIVED_REQUEST, PLAYER_PATTER, targetPlayer.getName()));
                        return;
                    }
                    player.sendMessage(languageManager.getMessageComponentReplace(Text.FRIEND_DECLINED_REQUEST, PLAYER_PATTER, targetPlayer.getName()));
                }
            } catch(FriendNotFoundException|AccountNotFoundException exception) {
                QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
        });
    }
}