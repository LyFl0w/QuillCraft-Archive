package net.quillcraft.parkourpvp.manager;

import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;
import net.quillcraft.core.utils.builders.scoreboard.ScoreboardBuilder;
import net.quillcraft.parkourpvp.ParkourPvP;
import net.quillcraft.parkourpvp.game.checkpoint.CheckPoint;
import net.quillcraft.parkourpvp.game.checkpoint.CheckPointCoinsBonus;
import net.quillcraft.parkourpvp.game.player.PlayerDataGame;

import net.quillcraft.parkourpvp.game.InGameStatus;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GameManager{

    private final ParkourPvP parkourPvP;

    private InGameStatus inGameStatus = InGameStatus.WAIT_LOBBY;

    private final HashMap<String, ScoreboardBuilder> scoreboardBuilderHashMap = new HashMap<>();
    private final HashMap<String, PlayerDataGame> playersData = new HashMap<>();

    private final ArrayList<CheckPoint> checkPoints = new ArrayList<>();
    private final ArrayList<Location> spawnPvP = new ArrayList<>();

    private final Location lobby;

    private final String defaultWorldName;
    private final World[] worlds;
    private final FileConfiguration fileConfiguration;

    public GameManager(ParkourPvP parkourPvP){
        this.parkourPvP = parkourPvP;
        this.lobby = new Location(parkourPvP.getServer().getWorld("LobbyParkourPvP"), 8.5, 103, 9.5, 135, 0);
        // TODO : DELETE COMMENTS WHEN WE HAVE MORE THAN ONE MAP
        //final List<String> worldsName = Arrays.asList("Natura", "Chronos", "Biomia");
        final List<String> worldsName = List.of("Natura");
        //Collections.shuffle(worldsName);
        this.defaultWorldName = worldsName.get(new SecureRandom().nextInt(worldsName.size()));
        LogManager.getLogger("Minecraft").info("World chose is "+defaultWorldName);
        this.worlds = new World[]{parkourPvP.getServer().createWorld(createNewWorld(defaultWorldName)),
                parkourPvP.getServer().createWorld(createNewWorld(defaultWorldName+"-PvP"))};
        this.fileConfiguration = new YamlConfigurationBuilder(parkourPvP,
                "game-settings-byworld/"+defaultWorldName+"-Settings.yml", true).getConfig();

        setWorldBorder();
        loadCheckPoints();
        loadSpawnPvP();
    }

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

    private void loadCheckPoints(){
        final ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("checkpoints");

        configurationSection.getKeys(false).forEach(key -> {
            final ConfigurationSection configurationPosition = configurationSection.getConfigurationSection(key);
            checkPoints.add(new CheckPoint(new Location(worlds[0], configurationPosition.getDouble("x"),
                    configurationPosition.getDouble("y"), configurationPosition.getDouble("z"),
                    (float) configurationPosition.getDouble("yaw"), (float)configurationPosition.getDouble("pitch")),
                    checkPoints.size()));
        });

        final ConfigurationSection coins_conf = fileConfiguration.getConfigurationSection("checkpoint_data");
        CheckPoint.coins = coins_conf.getInt("coins");
        CheckPointCoinsBonus.BONUS_FIRST.setAdditionalCoins(coins_conf.getInt("bonus.first"));
        CheckPointCoinsBonus.BONUS_SECOND.setAdditionalCoins(coins_conf.getInt("bonus.second"));
        CheckPointCoinsBonus.BONUS_THIRD.setAdditionalCoins(coins_conf.getInt("bonus.third"));
    }

    private void loadSpawnPvP(){
        final ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("pvp.spawn");

        configurationSection.getKeys(false).forEach(key -> {
            final ConfigurationSection configurationPosition = configurationSection.getConfigurationSection(key);
            spawnPvP.add(new Location(worlds[1], configurationPosition.getDouble("x"),
                    configurationPosition.getDouble("y"), configurationPosition.getDouble("z"),
                    (float) configurationPosition.getDouble("yaw"), (float) configurationPosition.getDouble("pitch")));
        });

        Collections.shuffle(spawnPvP, new SecureRandom());
    }

    private void setWorldBorder(){
        final ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("pvp.worldborder");
        final WorldBorder worldBorders = worlds[1].getWorldBorder();

        worldBorders.setCenter(configurationSection.getDouble("center.x"), configurationSection.getDouble("center.y"));
        worldBorders.setSize(configurationSection.getDouble("size"));
        worldBorders.setDamageBuffer(0.0d);
        worldBorders.setDamageAmount(1.0d);
        worldBorders.setWarningDistance(10);
    }

    public HashMap<String, ScoreboardBuilder> getScoreboardBuilderHashMap(){
        return scoreboardBuilderHashMap;
    }

    public ArrayList<CheckPoint> getCheckPoints(){
        return checkPoints;
    }

    public ArrayList<Location> getSpawnPvP(){
        return spawnPvP;
    }

    public HashMap<String, PlayerDataGame> getPlayersData(){
        return playersData;
    }

    public InGameStatus getInGameStatus(){
        return inGameStatus;
    }

    public Location getLobby(){
        return lobby;
    }

    public void setInGameStatus(InGameStatus inGameStatus){
        this.inGameStatus = inGameStatus;
    }

}
