package net.quillcraft.bungee.subscriber;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.game.Game;
import net.quillcraft.commons.game.Waiter;
import net.quillcraft.commons.game.WaitingList;
import net.quillcraft.commons.party.Party;
import net.quillcraft.commons.party.PartyProvider;

import org.redisson.api.RBucket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubscriberGame extends Subscriber{

    public void read(){
        redissonClient.getTopic("game.searchplayer").addListener(String.class, (channel, message) -> {
            final RBucket<? extends Game> gameRBucket = redissonClient.getBucket(message);
            final Game game = gameRBucket.get();
            final WaitingList waitingList = new WaitingList(game.getGameEnum());
            final List<UUID> futurPlayers = new ArrayList<>();

            waitingList.sortWaitersList();
            for(Waiter waiter : waitingList.getWaitersList()){
                if(game.getMaxPlayer() == futurPlayers.size()) break;

                final UUID playerUUID = waiter.getPlayer();
                if(waiter.hasParty()){
                    try{
                        final List<UUID> uuidList = new PartyProvider(new AccountProvider(playerUUID).getAccount()).getParty().getOnlineFollowersUUID();
                        if(game.getMaxPlayer() < futurPlayers.size() + uuidList.size() + 1) continue;
                        futurPlayers.addAll(uuidList);
                    }catch(AccountNotFoundException | PartyNotFoundException e){
                        e.printStackTrace();
                    }
                }
                futurPlayers.add(playerUUID);
                waitingList.getWaitersList().remove(waiter);
            }
            waitingList.updateWaitersListRedis();

            //TODO : set good string into getServerInfo method
            final ProxyServer proxyServer = ProxyServer.getInstance();
            final ServerInfo serverInfo = proxyServer.getServerInfo("srebrfihgjb");
            futurPlayers.stream().parallel().forEach(uuid -> proxyServer.getPlayer(uuid).connect(serverInfo));
        });
    }


}
