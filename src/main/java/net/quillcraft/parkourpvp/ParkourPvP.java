package net.quillcraft.parkourpvp;

import net.quillcraft.commons.game.GameProperties;
import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;
import net.quillcraft.core.utils.builders.scoreboard.ScoreboardBuilder;
import net.quillcraft.parkourpvp.manager.PluginManager;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class ParkourPvP extends JavaPlugin{

    private static ParkourPvP INSTANCE;

    private final HashMap<String, ScoreboardBuilder> scoreboardBuilderHashMap = new HashMap<>();
    private ParkourPvPGame parkourPvPGame;

    @Override
    public void onEnable(){
        INSTANCE = this;

        parkourPvPGame = new ParkourPvPGame(
                new YamlConfigurationBuilder(this, "default_game_setting.yml", true).getConfig().getInt("id"),
                new GameProperties(new YamlConfigurationBuilder(this, "game_properties.yml", true).getConfig()));

        new PluginManager(this);

        parkourPvPGame.setGameStatus(GeneralGameStatus.PLAYER_WAITING);
        parkourPvPGame.updateRedis();

        parkourPvPGame.searchPlayer();

        getLogger().info("Plugin ParkourPvP enable");
    }

    @Override
    public void onDisable(){
        parkourPvPGame.deleteRedisKey();
        getLogger().info("Plugin ParkourPvP disable");
    }

    public HashMap<String, ScoreboardBuilder> getScoreboardBuilderHashMap(){
        return scoreboardBuilderHashMap;
    }

    public ParkourPvPGame getParkourPvPGame(){
        return parkourPvPGame;
    }

    public static ParkourPvP getInstance(){
        return INSTANCE;
    }
}
