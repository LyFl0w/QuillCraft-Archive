package net.quillcraft.highblock;

import net.quillcraft.highblock.database.Database;

import net.quillcraft.highblock.manager.ChallengeManager;
import net.quillcraft.highblock.manager.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class HighBlock extends JavaPlugin {

    private static HighBlock INSTANCE;

    private Database database;

    private ChallengeManager challengeManager;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.database = new Database(this, "skyblock.db");

        new PluginManager(this);
        challengeManager = new ChallengeManager(this);
        challengeManager.init();
    }

    @Override
    public void onDisable() {
        try {
            database.closeConnection();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    public Database getDatabase() {
        return database;
    }

    public static HighBlock getInstance() {
        return INSTANCE;
    }
}