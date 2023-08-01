package net.quillcraft.build.listener.player;

import net.quillcraft.build.QuillCraftBuild;
import net.quillcraft.build.autosave.SaveTask;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final QuillCraftBuild main;

    public PlayerJoinListener(QuillCraftBuild main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        final World world = event.getPlayer().getWorld();
        final SaveTask saveTask = main.saveTask;

        saveTask.addWorld(world);

        if(saveTask.canSave()){
            saveTask.saveMaps(main.getServer().getWorldContainer().getPath()+"/save/"+java.time.LocalDate.now()+"/"+saveTask.getTime());
            new SaveTask(main);
        }
    }

}
