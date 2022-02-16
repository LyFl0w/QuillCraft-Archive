package net.quillcraft.parkourpvp;

import net.quillcraft.commons.game.GameProperties;
import net.quillcraft.commons.game.GeneralGameStatus;
import net.quillcraft.commons.game.ParkourPvPGame;
import net.quillcraft.core.utils.builders.YamlConfigurationBuilder;
import net.quillcraft.parkourpvp.manager.PluginManager;

import org.bukkit.plugin.java.JavaPlugin;

public class ParkourPvP extends JavaPlugin{

    private static ParkourPvP INSTANCE;

    private ParkourPvPGame parkourPvPGame;

    @Override
    public void onEnable(){
        INSTANCE = this;

        parkourPvPGame = new ParkourPvPGame(
                new GameProperties(new YamlConfigurationBuilder(this, "game_properties.yml", true).getConfig()));

        new PluginManager(this);

        parkourPvPGame.setGameStatus(GeneralGameStatus.PLAYER_WAITING);
        parkourPvPGame.updateRedis();
        parkourPvPGame.searchPlayer();

        getLogger().info("Plugin ParkourPvP enable");
    }

    @Override
    public void onDisable(){
        getLogger().info("Plugin ParkourPvP disable");
    }

    public ParkourPvPGame getParkourPvPGame(){
        return parkourPvPGame;
    }

    public static ParkourPvP getInstance(){
        return INSTANCE;
    }
}
