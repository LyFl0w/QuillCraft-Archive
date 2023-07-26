package net.quillcraft.core.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.management.redis.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

public class MessageCommand implements CommandExecutor {

    private final QuillCraftCore quillCraftCore;
    private final RedissonClient redissonClient;

    public MessageCommand(QuillCraftCore quillCraftCore) {
        this.quillCraftCore = quillCraftCore;
        this.redissonClient = RedisManager.MESSAGE.getRedisAccess().getRedissonClient();
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if(commandSender instanceof Player player && args.length > 1) {
            String targetPlayerName = args[0];

            if(player.getName().equalsIgnoreCase(targetPlayerName)) {
                player.sendMessage("Vous ne pouvez pas vous envoyer un message à vous même !");
                return true;
            }

            final Player targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
            final StringBuilder message = new StringBuilder();

            Arrays.stream(args).skip(1).forEach(secanteMessage -> message.append(" ").append(secanteMessage));

            if(targetPlayer != null && targetPlayer.isOnline()) {
                final UUID playerUUID = player.getUniqueId();
                final UUID targetPlayerUUID = targetPlayer.getUniqueId();

                targetPlayerName = targetPlayer.getName();
                targetPlayer.sendMessage("["+player.getName()+"->Moi]"+message);
                player.sendMessage("[Moi->"+targetPlayerName+"]"+message);

                final RSet<String> rSet = redissonClient.getSet(playerUUID.toString());
                if(!rSet.contains(targetPlayerUUID.toString())) {
                    rSet.clear();
                    rSet.add(targetPlayerUUID.toString());
                }
                rSet.expire(Duration.ofHours(2));

                final RSet<String> rSetTargetPlayer = redissonClient.getSet(targetPlayer.getUniqueId().toString());
                if(!rSetTargetPlayer.contains(playerUUID.toString())) {
                    rSetTargetPlayer.clear();
                    rSetTargetPlayer.add(playerUUID.toString());
                }
                rSetTargetPlayer.expire(Duration.ofHours(2));

                return true;
            }

            final ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("Message");
            out.writeUTF(targetPlayerName);
            out.writeUTF(message.toString());

            player.sendPluginMessage(quillCraftCore, "quillcraft:message", out.toByteArray());
            return true;
        }
        return false;
    }
}