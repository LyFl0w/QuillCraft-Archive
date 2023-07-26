package net.quillcraft.core.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.management.redis.RedisManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ReponseMessageCommand implements CommandExecutor {

    private final QuillCraftCore quillCraftCore;
    private final RedissonClient redissonClient;

    public ReponseMessageCommand(QuillCraftCore quillCraftCore) {
        this.quillCraftCore = quillCraftCore;
        this.redissonClient = RedisManager.MESSAGE.getRedisAccess().getRedissonClient();
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if(commandSender instanceof Player player && args.length > 0) {
            final RSet<String> rSet = redissonClient.getSet(player.getUniqueId().toString());
            if(rSet.isExists()) {
                final UUID playerUUID = player.getUniqueId();
                final UUID uuidTargetPlayer = UUID.fromString(new ArrayList<>(rSet.readAll()).get(0));
                final Player targetPlayer = quillCraftCore.getServer().getPlayer(uuidTargetPlayer);

                final StringBuilder message = new StringBuilder();

                Arrays.stream(args).forEach(secanteMessage -> message.append(" ").append(secanteMessage));

                if(targetPlayer != null && targetPlayer.isOnline()) {
                    final String targetPlayerName = targetPlayer.getName();
                    targetPlayer.sendMessage("["+player.getName()+"->Moi]"+message);
                    player.sendMessage("[Moi->"+targetPlayerName+"]"+message);

                    rSet.expire(Duration.ofHours(2));

                    final RSet<String> rSetTargetPlayer = redissonClient.getSet(uuidTargetPlayer.toString());
                    if(!rSetTargetPlayer.contains(playerUUID.toString())) {
                        rSetTargetPlayer.clear();
                        rSetTargetPlayer.add(playerUUID.toString());
                    }
                    rSetTargetPlayer.expire(Duration.ofHours(2));
                    return true;
                }

                final ByteArrayDataOutput out = ByteStreams.newDataOutput();

                out.writeUTF("Reponse");
                out.writeUTF(message.toString());

                player.sendPluginMessage(quillCraftCore, "quillcraft:message", out.toByteArray());
                return true;
            }
            player.sendMessage("Vous ne disposez d'aucune personne à qui vous pouvez répondre");
            return true;
        }
        return false;
    }
}