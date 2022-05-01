package net.quillcraft.parkourpvp.task.end;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.quillcraft.commons.game.GameEnum;
import net.quillcraft.commons.game.statistiques.PlayerGameStatistiqueProvider;
import net.quillcraft.commons.game.statistiques.parkourpvp.PlayerParkourPvPData;
import net.quillcraft.commons.game.statistiques.parkourpvp.PlayerParkourPvPStatistique;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.manager.GameManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EndTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;
    private final byte[] bungeeCordMessage;

    public EndTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.parkourPvP = ((EndTaskManager)customTaskManager).getJavaPlugin();
        this.time = 20;

        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("lobby_1");
        bungeeCordMessage = out.toByteArray();
    }

    @Override
    public void run(){

        if(time == 20){
            final Server server = parkourPvP.getServer();
            final GameManager gameManager = parkourPvP.getGameManager();
            final HashMap<String, PlayerParkourPvPData> playersDataGame = gameManager.getPlayersDataGame();
            final Supplier<Stream<PlayerParkourPvPData>> playerStream = () -> playersDataGame.values().stream();
            final Comparator<PlayerParkourPvPData> comparator = Comparator.comparing(PlayerParkourPvPData::getKill);

            final Optional<PlayerParkourPvPData> bestKillerOptional = playerStream.get().max(comparator);
            final Optional<PlayerParkourPvPData> worstKillerOptional = playerStream.get().min(comparator);

            if(bestKillerOptional.isPresent()){
                final PlayerParkourPvPData bestKiller = bestKillerOptional.get();
                if(bestKiller.getKill() != 0){
                    bestKiller.setBestKiller();
                    server.broadcastMessage(bestKiller.getPlayerName()+" a été le plus meurtrier est avec "+bestKiller.getKill()+" Kill"+(bestKiller.getKill() > 1 ? "s" : ""));

                    if(worstKillerOptional.isPresent()){
                        final PlayerParkourPvPData worstKiller = worstKillerOptional.get();
                        worstKiller.setWorstKiller();
                        server.broadcastMessage(worstKiller.getPlayerName()+" a été le moins meurtrier est avec "+worstKiller.getKill()+" Kill"+(worstKiller.getKill() > 1 ? "s" : ""));
                    }
                }
            }

            server.getScheduler().runTaskAsynchronously(parkourPvP, () -> {
                final String mapName = gameManager.getDefaultWorldName();
                final GameEnum gameEnum = GameEnum.PARKOUR_PVP_SOLO;
                playersDataGame.values().stream().parallel().forEach(playerParkourPvPData -> {
                    final PlayerGameStatistiqueProvider<PlayerParkourPvPStatistique> playerDataProvider = new PlayerGameStatistiqueProvider<>(parkourPvP, playerParkourPvPData.getUuid(), gameEnum);
                    playerDataProvider.getPlayerData().addStatistiques(mapName, playerParkourPvPData);
                    playerDataProvider.updatePlayerData();
                    playerDataProvider.expireRedis();
                });
            });
        }

        if(time == 15){
            final HashMap<String, PlayerParkourPvPData> playersData = parkourPvP.getGameManager().getPlayersDataGame();
            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(uuid -> {
                final Player player = parkourPvP.getServer().getPlayer(uuid);
                final PlayerParkourPvPData PlayerParkourPvPData = playersData.get(player.getName());
                final int kills = PlayerParkourPvPData.getKill();

                player.sendMessage("\nVous avez fait "+kills+" Kill"+(kills > 1 ? "s" : ""));
            });
        }

        if(time == 10) parkourPvP.getServer().getOnlinePlayers().forEach(player -> player.sendPluginMessage(parkourPvP, "BungeeCord", bungeeCordMessage));

        if(time == 0){
            cancel();
            parkourPvP.getServer().spigot().restart();
            return;
        }

        time--;
    }

}
