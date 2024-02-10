package net.quillcraft.bungee.messages;

import com.google.common.io.ByteArrayDataInput;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.quillcraft.bungee.serialization.QuillCraftBungee;
import net.quillcraft.bungee.data.redis.RedisManager;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.UUID;

public class MessageMessage extends Message {

    private final RedissonClient redissonClient;

    public MessageMessage(ProxyServer proxy, PluginMessageEvent event) {
        super(proxy, event);
        this.redissonClient = RedisManager.MESSAGE.getRedisAccess().getRedissonClient();
    }

    @Override
    protected void onPluginMessageRepPlayer(ProxiedPlayer player, String sub, ByteArrayDataInput in) {
        proxy.getScheduler().runAsync(QuillCraftBungee.getInstance(), () -> {
            if(sub.equals("Message")) {
                final ProxiedPlayer targetPlayer = proxy.getPlayer(in.readUTF());
                final String message = in.readUTF();

                if(targetPlayer == null) {
                    player.sendMessage(new TextComponent("Le joueur sélectionné n'est pas connecté !"));
                    return;
                }

                final String playerUUID = player.getUniqueId().toString();
                final String targetPlayerUUID = targetPlayer.getUniqueId().toString();

                targetPlayer.sendMessage(new TextComponent("["+player.getName()+"->Moi]"+message));
                player.sendMessage(new TextComponent("[Moi"+"->"+targetPlayer.getName()+"]"+message));

                final RSet<String> rSet = redissonClient.getSet(playerUUID);
                if(!rSet.contains(targetPlayerUUID)) {
                    rSet.clear();
                    rSet.add(targetPlayerUUID);
                }
                rSet.expire(Duration.ofHours(2));

                final RSet<String> rSetTargetPlayer = redissonClient.getSet(targetPlayerUUID);
                if(!rSetTargetPlayer.contains(playerUUID)) {
                    rSetTargetPlayer.clear();
                    rSetTargetPlayer.add(playerUUID);
                }
                rSetTargetPlayer.expire(Duration.ofHours(2));
                return;
            }
            if(sub.equals("Reponse")) {
                final RSet<String> rSet = redissonClient.getSet(player.getUniqueId().toString());

                final UUID uuidTargetPlayer = UUID.fromString((String) rSet.readAll().toArray()[0]);
                final ProxiedPlayer targetPlayer = proxy.getPlayer(uuidTargetPlayer);

                if(targetPlayer == null) {
                    player.sendMessage(new TextComponent("Le joueur n'est pas connecté !"));
                    return;
                }
                final String playerUUID = player.getUniqueId().toString();

                final String message = in.readUTF();

                targetPlayer.sendMessage(new TextComponent("["+player.getName()+"->Moi]"+message));
                player.sendMessage(new TextComponent("[Moi"+"->"+targetPlayer.getName()+"]"+message));

                rSet.expire(Duration.ofHours(2));

                final RSet<String> rSetTargetPlayer = redissonClient.getSet(uuidTargetPlayer.toString());
                if(!rSetTargetPlayer.contains(playerUUID)) {
                    rSetTargetPlayer.clear();
                    rSetTargetPlayer.add(playerUUID);
                }
                rSetTargetPlayer.expire(Duration.ofHours(2));

                return;
            }
            player.sendMessage(new TextComponent("Vous ne disposez d'aucune personne à qui vous pouvez répondre"));
        });
    }
}