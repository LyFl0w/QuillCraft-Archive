package net.quillcraft.parkourpvp.task.jump.wait.after;

import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.exception.TaskOverflowException;
import net.quillcraft.core.task.CustomTask;
import net.quillcraft.core.task.CustomTaskManager;
import net.quillcraft.core.utils.MessageUtils;
import net.quillcraft.core.utils.builders.ItemBuilder;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.inventory.shop.ShopCategoriesInventory;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.manager.TaskManager;
import net.quillcraft.parkourpvp.scoreboard.PvPScoreboard;
import net.quillcraft.parkourpvp.game.InGameStatus;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
            parkourPvP.getGameManager().getPlayersData().values().forEach(playerData -> {
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

            if(parkourPvPGame.getPlayerUUIDList().size() <= 1){
                // un joueur ou personne a gagné
                gameManager.setInGameStatus(InGameStatus.END);
                parkourPvPGame.setGameStatus(GeneralGameStatus.END);

                parkourPvPGame.updateRedis();

                Bukkit.broadcastMessage("§cIl ne reste plus personne, vous avez gagné par forfait !");

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

            parkourPvP.getParkourPvPGame().getPlayerUUIDList().forEach(playerUUID -> {
                final Player player = server.getPlayer(playerUUID);

                new PvPScoreboard(parkourPvP).setScoreboard(player);

                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().setItem(17, itemStack);
                player.teleport(spawnPvP.get(index.getAndIncrement()));
                player.openInventory(new ShopCategoriesInventory().getInventory(player));
                if(index.get() > spawnPvP.size()) index.set(0);
            });

            gameManager.setInGameStatus(InGameStatus.WAITING_BEFORE_PVP);

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
