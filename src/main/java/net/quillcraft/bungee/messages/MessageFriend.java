package net.quillcraft.bungee.messages;

import com.google.common.io.ByteArrayDataInput;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;

import net.quillcraft.bungee.data.management.redis.RedisManager;
import net.quillcraft.commons.friend.Friend;
import net.quillcraft.commons.friend.FriendProvider;

import org.redisson.api.RedissonClient;

public class MessageFriend extends Message {

    private final RedissonClient redissonClient;

    public MessageFriend(ProxyServer proxy, PluginMessageEvent event) {
        super(proxy, event);
        this.redissonClient = RedisManager.FRIEND_DATA.getRedisAccess().getRedissonClient();
    }

    @Override
    protected void onPluginMessageRepPlayer(ProxiedPlayer player, String sub, ByteArrayDataInput in) {
        //TODO: Faire fonctionner le code suivant
        if (sub.equalsIgnoreCase("List")) {

            final FriendProvider friendProvider = new FriendProvider(player);
            final Friend friend = friendProvider.getFriends();

            final StringBuilder message = new StringBuilder("Vos amis :");

            /*
            TODO : 1 - ajouter dans le string builder tous les amis (en ligne / hors ligne)
                   2 - recopier le plan de Party.java / MessageParty.java (line 147-166)
            */

            player.sendMessage(new TextComponent(message.toString()));

            return;
        }
        if (sub.equalsIgnoreCase("Add")) {

            return;
        }

        if (sub.equalsIgnoreCase("Accept")) {

            return;
        }

        if (sub.equalsIgnoreCase("Remove")) {

        }
    }
}