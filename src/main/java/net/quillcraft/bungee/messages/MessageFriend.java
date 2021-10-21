package net.quillcraft.bungee.messages;

import com.google.common.io.ByteArrayDataInput;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import org.redisson.api.RedissonClient;

public class MessageFriend extends Message{

    //TODO : private final RedissonClient redissonClient;
    public MessageFriend(ProxyServer proxy, PluginMessageEvent event){
        super(proxy, event);
        //TODO : this.redissonClient = RedisManager.FRIEND_DATA.getRedisAccess().getRedissonClient();
    }

    @Override
    protected void onPluginMessageRepPlayer(ProxiedPlayer player, String sub, ByteArrayDataInput in){
        if(sub.equalsIgnoreCase("List")){

            return;
        }
        if(sub.equalsIgnoreCase("Add")){

            return;
        }
        if(sub.equalsIgnoreCase("Remove")){

        }
    }
}
