package net.quillcraft.bungee.messages;

import com.google.common.io.ByteArrayDataInput;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.commons.game.*;
import net.quillcraft.commons.party.PartyProvider;

import org.redisson.api.RedissonClient;

import java.util.Collections;
import java.util.List;

public class MessageGame extends Message{

    private final RedissonClient redissonClient;

    public MessageGame(ProxyServer proxyServer, PluginMessageEvent event){
        super(proxyServer, event);
        this.redissonClient = RedisManager.GAME_SERVER.getRedisAccess().getRedissonClient();
    }

    @Override
    protected void onPluginMessageRepPlayer(ProxiedPlayer player, String sub, ByteArrayDataInput in){
        proxy.getScheduler().runAsync(QuillCraftBungee.getInstance(), () -> {
            try{
                final Account account = new AccountProvider(player).getAccount();

                try{
                    final List<ProxiedPlayer> playerList = (in.readBoolean()) ? new PartyProvider(account).getParty().getOnlinePlayers() : Collections.singletonList(player);

                    for(final String key : redissonClient.getKeys().getKeysByPattern(sub+":*")){
                        final Game game = (Game) redissonClient.getBucket(key).get();

                        if(game.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING)
                                && game.getGameProperties().getMaxPlayer()-game.getPlayerUUIDList().size() >= playerList.size()){
                            final ServerInfo serverInfo = proxy.getServerInfo(key);
                            playerList.stream().parallel().filter(players -> !players.getServer().getInfo().getName().equalsIgnoreCase(key))
                                    .forEach(players -> players.connect(serverInfo));
                            return;
                        }
                    }
                }catch(PartyNotFoundException e){
                    e.printStackTrace();
                }

                final GameEnum gameEnum = GameEnum.valueOf(sub);
                final WaitingList waitingList = new WaitingList(gameEnum);

                waitingList.getWaitersList().add(new Waiter(player.getUniqueId(), account.hasParty()));
                waitingList.updateWaitersListRedis();
                player.sendMessage(new TextComponent("§bVous avez rejoins une liste d'attente pour le jeu "+gameEnum.getGameName()));

            }catch(AccountNotFoundException e){
                e.printStackTrace();
            }

        });
    }

}