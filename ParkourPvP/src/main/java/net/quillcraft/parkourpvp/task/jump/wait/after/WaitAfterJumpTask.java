package net.quillcraft.parkourpvp.task.jump.wait.after;

import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.commons.game.status.GeneralGameStatus;
import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.core.utils.MessageUtils;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.InGameStatus;
import net.quillcraft.parkourpvp.inventory.shop.ShopCategoriesInventory;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.scoreboard.PvPScoreboard;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class WaitAfterJumpTask extends CustomTask{

    private final ParkourPvP parkourPvP;
    private int time;

    public WaitAfterJumpTask(CustomTaskManager customTaskManager){
        super(customTaskManager);
        this.parkourPvP = ((WaitAfterJumpTaskManager)customTaskManager).getJavaPlugin();
        this.time = 15;
    }

    @Override
    public void run(){

        if(time == 10){
            final Server server = parkourPvP.getServer();

            final String line = MessageUtils.line();
            parkourPvP.getGameManager().getPlayersDataGame().values().forEach(playerData -> {
                final Player player = server.getPlayer(playerData.getUuid());
                final StringBuilder jumpStatsDescribe = new StringBuilder(MessageUtils.chatCenteredMessage("§lStatistiques§f\n"));

                // Number of jumps
                jumpStatsDescribe.append(MessageUtils.chatCenteredMessage("Vous avez sauté "+playerData.getJump()+" fois\n"));

                // Number of times the player has fallen
                jumpStatsDescribe.append(MessageUtils.chatCenteredMessage("Vous êtes réapparus "+playerData.getRespawn()+" fois"));

                // Time to finish the parkour
                if(playerData.hasFinishParkour()) jumpStatsDescribe.append("\n")
                        .append(MessageUtils.chatCenteredMessage(playerData.getFormatedTimeToFinishParkour(new StringBuilder("Temps : ")).toString()));

                player.sendMessage(line+jumpStatsDescribe.append("\n")+line);
            });
        }

        if(time == 0){
            final Server server = parkourPvP.getServer();
            final GameManager gameManager = parkourPvP.getGameManager();
            final ParkourPvPGame parkourPvPGame = parkourPvP.getParkourPvPGame();
            final List<UUID> playersUUID = parkourPvPGame.getPlayerUUIDList();

            if(playersUUID.size() <= 1){
                // un joueur ou personne a gagné
                gameManager.setInGameStatus(InGameStatus.END);
                parkourPvPGame.setGameStatus(GeneralGameStatus.END);

                parkourPvPGame.updateRedis();

                if(playersUUID.size() == 1){
                    final Player player = server.getPlayer(playersUUID.get(0));

                    player.sendMessage("§cIl ne reste plus personne, vous avez gagné par forfait !");

                    gameManager.getPlayersDataGame().get(player.getName()).setWin();
                }

                try{
                    TaskManager.END_TASK_MANAGER.getCustomTaskManager().runTaskTimer(0L, 20L);
                }catch(TaskOverflowException e){
                    e.printStackTrace();
                }

                cancel();
                return;
            }

            final ArrayList<Location> spawnPvP = gameManager.getSpawnPvP();
            final ItemStack itemStack = new ItemBuilder(Material.NETHER_STAR).setName("§b§eSHOP").toItemStack();
            final AtomicInteger index = new AtomicInteger();
            final BukkitScheduler scheduler = parkourPvP.getServer().getScheduler();

            gameManager.setInGameStatus(InGameStatus.WAITING_BEFORE_PVP);

            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(playerUUID -> {
                final Player player = server.getPlayer(playerUUID);

                new PvPScoreboard(parkourPvP).setScoreboard(player);
                scheduler.runTaskLater(parkourPvP, () -> player.setGameMode(GameMode.SURVIVAL), 20L);

                player.getInventory().setItem(17, itemStack);
                player.teleport(spawnPvP.get(index.getAndIncrement()));
                player.openInventory(new ShopCategoriesInventory().getInventory(player));
                if(index.get() > spawnPvP.size()) index.set(0);
            });

            try{
                //Start Jump timer
                TaskManager.WAIT_BEFORE_PVP_TASK_MANAGER.getCustomTaskManager().runTaskTimer(0L, 20L);
            }catch(TaskOverflowException e){
                e.printStackTrace();
            }

            cancel();
            return;
        }

        time--;
    }

}
