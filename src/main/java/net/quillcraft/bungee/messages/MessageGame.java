package net.quillcraft.bungee.messages;

import com.google.common.io.ByteArrayDataInput;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.quillcraft.bungee.serialization.QuillCraftBungee;
import net.quillcraft.bungee.data.redis.RedisManager;
import net.quillcraft.bungee.subscriber.SubscriberGame;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.game.Game;
import net.quillcraft.commons.game.GameEnum;
import net.quillcraft.commons.game.status.GeneralGameStatus;
import net.quillcraft.commons.game.waiter.Waiter;
import net.quillcraft.commons.game.waiter.WaitingList;
import net.quillcraft.commons.party.PartyProvider;
import org.redisson.api.RedissonClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class MessageGame extends Message {

    private static final RedissonClient redissonClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();

    public MessageGame(ProxyServer proxyServer, PluginMessageEvent event) {
        super(proxyServer, event);
    }

    private static synchronized void synchronizedPlayerRep(ProxyServer proxyServer, ProxiedPlayer player, String sub, boolean hasParty) {

        final GameEnum gameEnum = GameEnum.getGamePlayerWaiting(player.getUniqueId());
        if(gameEnum != null) {
            // Si il attend déjà sur le meme jeu, alors ne rien faire
            if(gameEnum == GameEnum.valueOf(sub)) return;

            GameEnum.removePlayerWaiting(player.getUniqueId());
            player.sendMessage(new TextComponent("§cVous avez quitté la file d'attente de "+gameEnum.getGameName()));
        }

        // Si un jeu est entrain ou va se faire traiter
        if(SubscriberGame.getPublishGameLinkedQueue().stream().parallel().anyMatch(s -> s.startsWith(sub+":"))) {
            addPlayerToWaiter(sub, player, hasParty);
            return;
        }

        try {
            final List<ProxiedPlayer> playerList = (hasParty) ? new PartyProvider(new AccountProvider(player).getAccount()).getParty().getOnlinePlayers() : Collections.singletonList(player);

            final Optional<String> serversName = redissonClient.getKeys().getKeysStreamByPattern(sub+":*").parallel().filter(key -> {
                final Game game = (Game) redissonClient.getBucket(key).get();
                return game.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING) && game.getGameProperties().getMaxPlayer()-game.getPlayerUUIDList().size() >= playerList.size();
            }).max(Comparator.comparing(key -> ((Game) redissonClient.getBucket(key).get()).getPlayerUUIDList().size()));

            if(serversName.isPresent()) {
                final String serverName = serversName.get();
                final ServerInfo serverInfo = proxyServer.getServerInfo(serverName);
                playerList.stream().parallel().filter(players -> !players.getServer().getInfo().getName().equalsIgnoreCase(serverName)).forEach(players -> players.connect(serverInfo));
            } else {
                addPlayerToWaiter(sub, player, hasParty);
            }
        } catch(AccountNotFoundException|PartyNotFoundException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private static void addPlayerToWaiter(String sub, ProxiedPlayer player, boolean hasParty) {
        final GameEnum gameEnum = GameEnum.valueOf(sub);
        final WaitingList waitingList = new WaitingList(gameEnum);

        waitingList.getWaitersList().add(new Waiter(player.getUniqueId(), hasParty));
        waitingList.updateWaitersListRedis();
        player.sendMessage(new TextComponent("§bVous avez rejoins une liste d'attente pour le jeu "+gameEnum.getGameName()));
    }

    @Override
    protected void onPluginMessageRepPlayer(ProxiedPlayer player, String sub, ByteArrayDataInput in) {
        proxy.getScheduler().runAsync(QuillCraftBungee.getInstance(), () -> MessageGame.synchronizedPlayerRep(proxy, player, sub, in.readBoolean()));
    }

}