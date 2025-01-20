package net.quillcraft.bungee.subscriber;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.game.Game;
import net.quillcraft.commons.game.waiter.Waiter;
import net.quillcraft.commons.game.waiter.WaitingList;
import net.quillcraft.commons.party.PartyProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class SubscriberGame extends Subscriber {

    private static final ConcurrentLinkedQueue<String> linkedQueue = new ConcurrentLinkedQueue<>();

    public SubscriberGame(ProxyServer proxyServer) {
        super(proxyServer);
    }

    public static Queue<String> getPublishGameLinkedQueue() {
        return linkedQueue;
    }

    public void read() {
        redissonClient.getTopic("game.searchplayer").addListener(String.class, (channel, message) -> linkedQueue.offer(message));

        new Thread(() -> {
            while(true) {
                if(linkedQueue.isEmpty()) continue;
                final String message = linkedQueue.peek();

                QuillCraftBungee.getInstance().getLogger().info("Game server pub : "+message);

                final Game game = (Game) redissonClient.getBucket(message).get();
                final WaitingList waitingList = new WaitingList(game.getGameEnum());

                if(waitingList.getWaitersList().isEmpty()) {
                    linkedQueue.poll();
                    continue;
                }

                final ArrayList<ProxiedPlayer> futurPlayers = new ArrayList<>();
                final ArrayList<Waiter> toRemove = new ArrayList<>();

                final int maxPlayer = game.getGameProperties().getMaxPlayer()-game.getPlayerUUIDList().size();

                waitingList.sortWaitersList();
                for(Waiter waiter : waitingList.getWaitersList()) {
                    if(maxPlayer == futurPlayers.size()) break;

                    final ProxiedPlayer proxiedPlayer = proxyServer.getPlayer(waiter.getPlayerUUID());
                    //if(proxiedPlayer != null){
                    if(waiter.hasParty()) {
                        try {
                            final List<ProxiedPlayer> playerStream = new PartyProvider(new AccountProvider(waiter.getPlayerUUID()).getAccount()).getParty().getOnlinePlayers().stream().filter(proxiedPlayers -> !proxiedPlayers.getServer().getInfo().getName().equalsIgnoreCase(message)).toList();
                            if(maxPlayer < futurPlayers.size()+playerStream.size()) continue;
                            futurPlayers.addAll(playerStream);
                        } catch(AccountNotFoundException|PartyNotFoundException exception) {
                            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
                        }
                    } else {
                        futurPlayers.add(proxiedPlayer);
                    }
                    //}
                    toRemove.add(waiter);
                }
                linkedQueue.poll();

                waitingList.getWaitersList().removeAll(toRemove);
                waitingList.updateWaitersListRedis();

                final ServerInfo serverInfo = proxyServer.getServerInfo(message);
                futurPlayers.stream().parallel().filter(Objects::nonNull).forEach(proxiedPlayer -> proxiedPlayer.connect(serverInfo));
            }
        }).start();
    }
}