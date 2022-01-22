package net.quillcraft.parkourpvp;

import net.quillcraft.commons.game.GameProperties;
import net.quillcraft.commons.game.GameStatus;
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
                new GameProperties(new YamlConfigurationBuilder(this, "game_properties.yaml", true).getConfig()));

        new PluginManager(this);

        parkourPvPGame.setGameStatus(GameStatus.PLAYER_WAITING);

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
