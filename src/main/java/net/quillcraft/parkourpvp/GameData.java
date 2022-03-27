package net.quillcraft.parkourpvp;

import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

public class GameData{

    private final ParkourPvP parkourPvP;

    private final String defaultWorldName;
    private final World[] worlds;
    private final FileConfiguration fileConfiguration;

    public GameData(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;

        // TODO : DELETE COMMENTS WHEN WE HAVE MORE THAN ONE MAP
        //final List<String> worldsName = Arrays.asList("Natura", "Chronos", "Biomia");
        final List<String> worldsName = List.of("Natura");
        //Collections.shuffle(worldsName);
        this.defaultWorldName = worldsName.get(new SecureRandom().nextInt(worldsName.size()));
        LogManager.getLogger("Minecraft").info("World chose is "+defaultWorldName);
        this.worlds = new World[]{parkourPvP.getServer().createWorld(createNewWorld(defaultWorldName)),
                parkourPvP.getServer().createWorld(createNewWorld(defaultWorldName+"-PvP"))};
        this.fileConfiguration = new YamlConfigurationBuilder(parkourPvP, "game-settings-byworld/"+defaultWorldName+"-Settings", true).getConfig();
    }

    public void onDisable(){
        final Server server = parkourPvP.getServer();
        final File worldDir = server.getWorldContainer();
        final BukkitScheduler scheduler = server.getScheduler();
        for(final World world : worlds){
            scheduler.runTaskAsynchronously(parkourPvP, () -> {
                server.unloadWorld(world, true);
                new File(worldDir, world.getName()).delete();
            });
        }
    }

    public String getDefaultWorldName(){
        return defaultWorldName;
    }

    public World[] getWorlds(){
        return worlds;
    }

    public FileConfiguration getFileConfiguration(){
        return fileConfiguration;
    }

    @Nullable
    private WorldCreator createNewWorld(@Nonnull String worldName){
        final File worldDir = parkourPvP.getServer().getWorldContainer();
        final String worldNameCopy = worldName + "_ig";
        final File newWorldFile = new File(worldDir.getParent(), worldNameCopy);

        if(newWorldFile.exists()) newWorldFile.delete();
        try{
            FileUtils.copyDirectory(worldDir, newWorldFile);
            return new WorldCreator(worldNameCopy);
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }



}
