package net.quillcraft.lobby.subscriber;

// TODO : REWRITE THE ENTIER CORE OF THIS CLASS WITH GENERIC METHOD (LOOK BUNGEECORD SUBRSCRIBER) 💖💝💓

import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.lobby.QuillCraftLobby;
import org.redisson.api.RedissonClient;

public class ScoreboardSubscriber {

    private final QuillCraftLobby quillCraftLobby;
    private final RedissonClient redissonClient;

    public ScoreboardSubscriber(QuillCraftLobby quillCraftLobby) {
        this.quillCraftLobby = quillCraftLobby;
        this.redissonClient = RedisManager.WEB_API.getRedisAccess().getRedissonClient();
        read();
    }

    private void read() {
        redissonClient.getTopic("players.size.update").addListener(Integer.class, (channel, message) -> quillCraftLobby.getServer().getScheduler().runTask(quillCraftLobby, () -> quillCraftLobby.getScoreboardManager().getAllScoreboardBuilders().forEach(scoreboardBuilder -> scoreboardBuilder.setLine("Connecté: "+redissonClient.getAtomicLong("players.size").get(), 6))));
    }


}
