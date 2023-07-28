package net.quillcraft.core.data.management.sql;

import net.quillcraft.commons.game.GameEnum;
import net.quillcraft.commons.game.statistiques.PlayerGameStatistiqueProvider;
import net.quillcraft.core.manager.configuration.ConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public enum DatabaseManager {

    MINECRAFT_SERVER(getDatabaseCredentials("minecraft_data")),
    STATISTIQUES(getDatabaseCredentials("statistiques"));

    private final DatabaseAccess databaseAccess;

    DatabaseManager(final DatabaseCredentials credentials) {
        this.databaseAccess = new DatabaseAccess(credentials);
    }

    public static void initAllDatabaseConnections() {
        for(DatabaseManager databaseManager : values()) {
            databaseManager.getDatabaseAccess().initPool();
        }
    }

    public static void closeAllDatabaseConnections() {
        for(DatabaseManager databaseManager : values()) {
            databaseManager.getDatabaseAccess().closePool();
        }
    }

    public static void createAllTable() {
        Arrays.stream(GameEnum.values()).parallel().forEach(PlayerGameStatistiqueProvider::createTableIfNotExist);
    }

    private static FileConfiguration getConfiguration() {
        return ConfigurationManager.DATA_ACCESS.getConfiguration();
    }

    public final DatabaseAccess getDatabaseAccess() {
        return databaseAccess;
    }

    private static DatabaseCredentials getDatabaseCredentials(String name) {
        final String path = "mysql."+name+".";
        return new DatabaseCredentials(
                getConfiguration().getString(path+"host"),
                getConfiguration().getString(path+"user.name"),
                getConfiguration().getString(path+"user.password"),
                getConfiguration().getString(path+"database_name"),
                getConfiguration().getInt(path+"port")
        );
    }

}
