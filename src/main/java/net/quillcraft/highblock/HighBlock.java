package net.quillcraft.highblock;

import net.quillcraft.highblock.database.Database;
import net.quillcraft.highblock.manager.ChallengeManager;
import net.quillcraft.highblock.manager.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class HighBlock extends JavaPlugin {

    private static HighBlock instance;

    private Database database;

    private ChallengeManager challengeManager;

    public static HighBlock getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.database = new Database(this, "highblock.db");

        new PluginManager(this);
        challengeManager = new ChallengeManager(this);
        challengeManager.init();
    }

    @Override
    public void onDisable() {
        try {
            database.closeConnection();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    public Database getDatabase() {
        return database;
    }
}