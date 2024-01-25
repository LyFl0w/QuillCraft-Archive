package net.quillcraft.build.autosave;

import net.quillcraft.build.QuillCraftBuild;
import net.quillcraft.build.manager.ConfigurationManager;

import net.quillcraft.build.util.FormattedNumber;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SaveTask extends BukkitRunnable {

    private static boolean canSave = false;
    private static final String NAME = ChatColor.GOLD + "[" + ChatColor.AQUA + "AutoSave" + ChatColor.GOLD + "]§f ";
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

    private final QuillCraftBuild quillCraftBuild;
    private long deleteAfter;
    private final String serverPath;

    public SaveTask(QuillCraftBuild quillCraftBuild) {
        this.quillCraftBuild = quillCraftBuild;

        this.serverPath = quillCraftBuild.getServer().getWorldContainer().getPath();

        final SaveTask oldTask = quillCraftBuild.saveTask;
        if (oldTask != null && oldTask != this && ! oldTask.isCancelled()) oldTask.cancel();

        quillCraftBuild.saveTask = this;

        try {
            final long delay = 20L * FormattedNumber.getValueFromFormattedText(
                    ConfigurationManager.AUTO_SAVE.getConfiguration().getString("timer"));

            this.deleteAfter = FormattedNumber.getValueFromFormattedText(
                    ConfigurationManager.AUTO_SAVE.getConfiguration().getString("delete_after")) * 1000L;

            this.runTaskTimer(quillCraftBuild, delay, delay);

        } catch (Exception e) {
            quillCraftBuild.getLogger().log(Level.SEVERE, e.getMessage(), e);
            quillCraftBuild.getServer().shutdown();
        }
    }

    @Override
    public void run() {
        if (!quillCraftBuild.getServer().getOnlinePlayers().isEmpty()) {
            saveAndDelete();
        } else {
            canSave = true;
            cancel();
        }
    }

    public void saveAndDelete() {
        final Date date = new Date();
        final String path = serverPath + "/save/";

        deleteOldSave(path);
        saveMaps(path + sdfDate.format(date) + "/" + sdfTime.format(date));
    }


    private void saveMaps(String path) {
        final Server server = quillCraftBuild.getServer();

        server.getScheduler().runTaskAsynchronously(quillCraftBuild, () -> {
            canSave = false;
            final long timeCalcul = System.currentTimeMillis();
            server.broadcastMessage(NAME + ChatColor.GREEN + "Sauvegade des mondes en cours");
            new File(path).mkdirs();
            server.getWorlds().stream().parallel().forEach(world ->
                    ZipCompress.zip4j(path + "/" + world.getName() + "_temps", world.getWorldFolder(), quillCraftBuild.getLogger()));
            server.broadcastMessage(NAME + ChatColor.GREEN + "Sauvegade des mondes effectué (" + (System.currentTimeMillis() - timeCalcul) / 1000 + "'s)");
        });

    }

    private void deleteOldSave(String path) {
        final Logger logger = quillCraftBuild.getLogger();

        final File saveDirectory = new File(path);
        final long minimumTime = System.currentTimeMillis() - deleteAfter;

        try {
            for (final File oldSave : saveDirectory.listFiles()) {
                final String oldSaveName = oldSave.getName();
                if(sdfDate.parse(oldSaveName).getTime() <= minimumTime) {
                    FileUtils.deleteDirectory(oldSave);
                    logger.info("Delete "+oldSaveName);
                }
            }
        } catch (ParseException | IOException exception) {
            quillCraftBuild.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    public boolean canSave() {
        return canSave;
    }

}
