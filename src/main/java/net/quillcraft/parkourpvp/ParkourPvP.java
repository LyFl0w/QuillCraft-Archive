package net.quillcraft.parkourpvp;

import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.commons.game.properties.GameProperties;
import net.quillcraft.commons.game.status.GeneralGameStatus;
import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;
import net.quillcraft.parkourpvp.manager.GameManager;
import net.quillcraft.parkourpvp.manager.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ParkourPvP extends JavaPlugin{

    private static ParkourPvP INSTANCE;

    private GameManager gameManager;

    private ParkourPvPGame parkourPvPGame;

    @Override
    public void onEnable(){
        INSTANCE = this;

        parkourPvPGame = new ParkourPvPGame(
                new YamlConfigurationBuilder(this, "default_game_setting.yml", true).getConfig().getInt("id"),
                new GameProperties(new YamlConfigurationBuilder(this, "game_properties.yml", true).getConfig()));

        gameManager = new GameManager(this);

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

    public ParkourPvPGame getParkourPvPGame(){
        return parkourPvPGame;
    }

    public static ParkourPvP getInstance(){
        return INSTANCE;
    }

    public GameManager getGameManager(){
        return gameManager;
    }
}
