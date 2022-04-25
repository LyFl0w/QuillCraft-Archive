package net.quillcraft.parkourpvp.listener.player;

import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.scoreboard.JumpScoreboard;
import net.quillcraft.parkourpvp.scoreboard.LobbyScoreboard;
import net.quillcraft.parkourpvp.scoreboard.PvPScoreboard;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class PlayerQuitListener implements Listener{

    private final ParkourPvP parkourPvP;
    public PlayerQuitListener(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
    }
    
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        final Player player = event.getPlayer();
        final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();

        final List<UUID> playerList = parkourPvPGame.getPlayerUUIDList();
        if(!playerList.contains(player.getUniqueId())) return;

        playerList.remove(player.getUniqueId());

        final GameManager gameManager = parkourPvP.getGameManager();
        final String playerName = player.getName();

        gameManager.getScoreboardBuilderHashMap().remove(playerName);

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING_FULL)){
            if(!parkourPvPGame.isFullyFilled()) parkourPvPGame.setGameStatus(GeneralGameStatus.PLAYER_WAITING);
        }else if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.IN_GAME)){
            parkourPvP.getServer().broadcastMessage("§c"+playerName+" a quitté le jeu !");

            switch(gameManager.getInGameStatus()){
                case WAITING_BEFORE_JUMP -> {
                    gameManager.getPlayersData().remove(playerName);
                    new JumpScoreboard(parkourPvP).updatePlayersSize();
                }
                case JUMP, WAITING_AFTER_JUMP -> new JumpScoreboard(parkourPvP).updatePlayersSize();
                case PVP, WAITING_BEFORE_PVP -> {
                    new PvPScoreboard(parkourPvP).updatePlayersSize();

                    final Supplier<Stream<? extends Player>> survivePlayers = () -> parkourPvP.getServer().getOnlinePlayers().stream().filter(players -> players.getGameMode() == GameMode.SURVIVAL);
                    if(survivePlayers.get().count() == 1){
                        final Player winner = survivePlayers.get().findFirst().get();
                        parkourPvP.getGameManager().getPlayersData().get(winner.getName()).setWin();

                        winner.setGameMode(GameMode.CREATIVE);
                        winner.sendMessage("You win");
                    }else if(survivePlayers.get().count() == 0){
                        parkourPvP.getServer().broadcastMessage("§cTout le monde est mort !\nPersonne n'a gagné ");
                    }
                }
            }

        }
        parkourPvPGame.updateRedis();

        if(parkourPvPGame.actualGameStatusIs(GeneralGameStatus.PLAYER_WAITING)){
            new LobbyScoreboard(parkourPvP).updatePlayersSize();
            parkourPvPGame.searchPlayer();
        }

        event.setQuitMessage("");
    }

}
