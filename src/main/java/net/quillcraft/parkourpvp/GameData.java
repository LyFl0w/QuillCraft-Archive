package net.quillcraft.parkourpvp;

import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

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
        this.fileConfiguration = new YamlConfigurationBuilder(parkourPvP, "game-settings-byworld/"+defaultWorldName+"-Settings.yml", true).getConfig();
    }

    @SuppressWarnings({})
    public void onDisable(){
        final Server server = parkourPvP.getServer();
        final File worldDir = server.getWorldContainer();
        for(final World world : worlds){
            server.unloadWorld(world, true);
            new File(worldDir, world.getName()).delete();
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
        final File world = new File(parkourPvP.getServer().getWorldContainer(), worldName);
        final File newWorldFile = new File(world.getParent(), worldName + "_ig");

        if(newWorldFile.exists()) newWorldFile.delete();
        try{
            FileUtils.copyDirectory(world, newWorldFile);
            return new WorldCreator(newWorldFile.getName());
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }



}
