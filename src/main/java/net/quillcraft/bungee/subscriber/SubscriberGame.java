package net.quillcraft.bungee.subscriber;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.game.Game;
import net.quillcraft.commons.game.Waiter;
import net.quillcraft.commons.game.WaitingList;
import net.quillcraft.commons.party.PartyProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SubscriberGame extends Subscriber{

    private final static ConcurrentLinkedQueue<String> linkedQueue = new ConcurrentLinkedQueue<>();

    public SubscriberGame(ProxyServer proxyServer){
        super(proxyServer);
    }

    public static ConcurrentLinkedQueue<String> getPublishGameLinkedQueue(){
        return linkedQueue;
    }

    public void read(){
        redissonClient.getTopic("game.searchplayer").addListener(String.class, (channel, message) -> linkedQueue.offer(message));

        new Thread(() -> {
            while(true){
                if(linkedQueue.size() == 0) continue;
                final String message = linkedQueue.peek();

                QuillCraftBungee.getInstance().getLogger().info("Game server pub : "+message);

                final Game game = (Game) redissonClient.getBucket(message).get();
                final WaitingList waitingList = new WaitingList(game.getGameEnum());

                if(waitingList.getWaitersList().size() == 0) continue;

                final ArrayList<ProxiedPlayer> futurPlayers = new ArrayList<>();
                final ArrayList<Waiter> toRemove = new ArrayList<>();

                final int maxPlayer = game.getGameProperties().getMaxPlayer()-game.getPlayerUUIDList().size();

                waitingList.sortWaitersList();
                for(Waiter waiter : waitingList.getWaitersList()){
                    if(maxPlayer == futurPlayers.size()) break;

                    if(waiter.hasParty()){
                        try{
                            final List<ProxiedPlayer> playerStream = new PartyProvider(new AccountProvider(waiter.getPlayerUUID()).getAccount()).getParty().getOnlinePlayers().stream().filter(proxiedPlayer -> !proxiedPlayer.getServer().getInfo().getName().equalsIgnoreCase(message)).toList();
                            if(maxPlayer < futurPlayers.size()+playerStream.size()) continue;
                            futurPlayers.addAll(playerStream);
                        }catch(AccountNotFoundException|PartyNotFoundException e){
                            e.printStackTrace();
                        }
                    }else{
                        futurPlayers.add(proxyServer.getPlayer(waiter.getPlayerUUID()));
                    }
                    toRemove.add(waiter);
                }
                waitingList.getWaitersList().removeAll(toRemove);
                waitingList.updateWaitersListRedis();

                final ServerInfo serverInfo = proxyServer.getServerInfo(message);
                futurPlayers.stream().parallel().forEach(proxiedPlayer -> proxiedPlayer.connect(serverInfo));

                linkedQueue.poll();
            }
        }).start();
    }
}