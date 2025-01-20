package net.quillcraft.bungee.data.sql;

import net.md_5.bungee.config.Configuration;
import net.quillcraft.bungee.manager.configuration.ConfigurationManager;

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

    private static Configuration getConfiguration() {
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
