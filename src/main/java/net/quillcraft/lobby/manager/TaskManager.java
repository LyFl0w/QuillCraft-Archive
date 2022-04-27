package net.quillcraft.lobby.manager;

import net.quillcraft.lobby.QuillCraftLobby;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TaskManager {

    private final QuillCraftLobby quillCraftLobby;
    private final List<BukkitTask> taskInProcess;
    public TaskManager(QuillCraftLobby quillCraftLobby){
        this.quillCraftLobby = quillCraftLobby;
        this.taskInProcess = new ArrayList<>();
        onEnableTasks();
    }

    private void onEnableTasks(){
        // TODO : ADD NEW SYSTEM TASK
        // taskInProcess.add(new AutoMessageTask(quillCraftLobby).runTaskTimerAsynchronously(quillCraftLobby, 120L, 20L*60));
        taskInProcess.add(quillCraftLobby.getServer().getScheduler().runTaskTimer(quillCraftLobby, ()->{
            final FileConfiguration muguetConfiguration = ConfigurationManager.MUGUET.getConfiguration();
            final Set<String> timers =  muguetConfiguration.getKeys(false);
            for(String timer : timers){
                long timerconverse = Long.parseLong(timer);
                if(timerconverse < System.currentTimeMillis()){
                    for(String i : muguetConfiguration.getConfigurationSection(timer+ ".").getKeys(false)){
                        Location location = muguetConfiguration.getLocation(timer + "." + i);
                        location.getWorld().getBlockAt(location).setType(Material.LILY_OF_THE_VALLEY);
                        quillCraftLobby.getLogger().warning("timer path : "+timer);
                        muguetConfiguration.set(timer, null);
                        quillCraftLobby.getLogger().warning("timer key : "+muguetConfiguration.get(timer));
                    }
                    ConfigurationManager.MUGUET.saveFile();
                }
            }
        },0L, 1200L));

    }

    public void onDisableTasks(){
        taskInProcess.forEach(BukkitTask::cancel);
    }


}
