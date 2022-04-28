package net.quillcraft.parkourpvp.task.end;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.player.PlayerDataGame;
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
            final Supplier<Stream<PlayerDataGame>> playerStream = () -> parkourPvP.getGameManager().getPlayersDataGame().values().stream();
            final Comparator<PlayerDataGame> comparator = Comparator.comparing(PlayerDataGame::getKill);

            final Optional<PlayerDataGame> bestKillerOptional = playerStream.get().max(comparator);
            final Optional<PlayerDataGame> worstKillerOptional = playerStream.get().min(comparator);

            if(bestKillerOptional.isPresent()){
                final PlayerDataGame bestKiller = bestKillerOptional.get();
                if(bestKiller.getKill() == 0) return;
                bestKiller.setBestKiller();
                server.broadcastMessage(bestKiller.getPlayerName()+" a été le plus meurtrier est avec "+bestKiller.getKill()+" Kill"+(bestKiller.getKill() > 1 ? "s" : ""));


                if(worstKillerOptional.isPresent()){
                    final PlayerDataGame worstKiller = worstKillerOptional.get();
                    worstKiller.setWorstKiller();
                    server.broadcastMessage(worstKiller.getPlayerName()+" a été le moins meurtrier est avec "+worstKiller.getKill()+" Kill"+(worstKiller.getKill() > 1 ? "s" : ""));
                }
            }
        }

        if(time == 15){
            final HashMap<String, PlayerDataGame> playersData = parkourPvP.getGameManager().getPlayersDataGame();
            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(uuid -> {
                final Player player = parkourPvP.getServer().getPlayer(uuid);
                final PlayerDataGame playerDataGame = playersData.get(player.getName());
                final int kills = playerDataGame.getKill();

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
