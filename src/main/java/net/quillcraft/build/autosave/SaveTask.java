package net.quillcraft.build.autosave;

import net.quillcraft.build.QuillCraftBuild;
import net.quillcraft.build.manager.ConfigurationManager;

import net.quillcraft.build.util.FormattedNumber;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SaveTask extends BukkitRunnable {

    private static boolean canSave = false;
    private static final String name = ChatColor.GOLD + "[" + ChatColor.AQUA + "AutoSave" + ChatColor.GOLD + "]§f ";

    private final QuillCraftBuild quillCraftBuild;
    private final List<World> toSave;
    private int deleteAfter;
    private final String serverPath;

    public SaveTask(QuillCraftBuild quillCraftBuild) {
        this.quillCraftBuild = quillCraftBuild;
        this.toSave = new ArrayList<>();

        this.serverPath = quillCraftBuild.getServer().getWorldContainer().getPath();

        if (quillCraftBuild.saveTask != null && quillCraftBuild.saveTask != this) quillCraftBuild.saveTask.cancel();
        quillCraftBuild.saveTask = this;

        try {
            final long delay = 20L * FormattedNumber.getValueFromFormattedText(
                    ConfigurationManager.AUTO_SAVE.getConfiguration().getString("timer"));

            this.deleteAfter = FormattedNumber.getValueFromFormattedText(
                    ConfigurationManager.AUTO_SAVE.getConfiguration().getString("delete_after"));

            this.runTaskTimer(quillCraftBuild, delay, delay);
        } catch (Exception e) {
            quillCraftBuild.getLogger().log(Level.SEVERE, e.getMessage(), e);
            quillCraftBuild.getServer().shutdown();
        }
    }

    @Override
    public void run() {
        if (!quillCraftBuild.getServer().getOnlinePlayers().isEmpty()) {
            saveMaps(serverPath + "/save/" + LocalDate.now() + "/" + getTime());
        } else {
            canSave = true;
        }
    }

    public void saveMaps(String path) {
        final Server server = quillCraftBuild.getServer();

        server.getScheduler().runTaskAsynchronously(quillCraftBuild, () -> {
            canSave = false;
            final long timeCalcul = System.currentTimeMillis();
            server.broadcastMessage(name + ChatColor.GREEN + "Sauvegade des mondes en cours");
            new File(path).mkdirs();
            toSave.stream().parallel().forEach(world ->
                    ZipCompress.zip4j(path + "/" + world.getName() + "_temps", world.getWorldFolder(), quillCraftBuild.getLogger()));
            server.broadcastMessage(name + ChatColor.GREEN + "Sauvegade des mondes effectué (" + (System.currentTimeMillis() - timeCalcul) / 1000 + "'s)");
        });

        toSave.clear();

        for (Player player : server.getOnlinePlayers()) {
            addWorld(player.getWorld());
        }
    }

    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now).replaceFirst(":", "h");
    }

    public boolean canSave() {
        return canSave;
    }

    public void addWorld(World world) {
        if (!toSave.contains(world)) toSave.add(world);
    }

    public List<String> getOldMap() {
        final long minimumTime = System.currentTimeMillis() - 1000L * deleteAfter;



        return null;
    }
}
