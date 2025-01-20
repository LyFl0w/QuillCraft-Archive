package net.quillcraft.build.listener.player;

import net.quillcraft.build.QuillCraftBuild;
import net.quillcraft.build.autosave.SaveTask;

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
        final SaveTask saveTask = main.getSaveTask();

        if(saveTask.canSave()){
            saveTask.saveAndDelete();
            new SaveTask(main);
        }
    }

}
