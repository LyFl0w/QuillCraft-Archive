package net.quillcraft.core.data.management.sql;

import net.quillcraft.core.manager.ConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;

public enum DatabaseManager {

    MINECRAFT_SERVER(new DatabaseCredentials(getConfiguration().getString("mysql.minecraft_data.host"),
            getConfiguration().getString("mysql.minecraft_data.user.name"), getConfiguration().getString("mysql.minecraft_data.user.password"),
            getConfiguration().getString("mysql.minecraft_data.database_name"), getConfiguration().getInt("mysql.minecraft_data.port")));

    private final DatabaseAccess databaseAccess;

    DatabaseManager(final DatabaseCredentials credentials){
        this.databaseAccess = new DatabaseAccess(credentials);
    }

    public final DatabaseAccess getDatabaseAccess(){
        return databaseAccess;
    }

    public static void initAllDatabaseConnections(){
        for(DatabaseManager databaseManager : values()){
            databaseManager.getDatabaseAccess().initPool();
        }
    }

    public static void closeAllDatabaseConnections(){
        for(DatabaseManager databaseManager : values()){
            databaseManager.getDatabaseAccess().closePool();
        }
    }

    private static FileConfiguration getConfiguration(){
        return ConfigurationManager.DATA_ACCESS.getConfiguration();
    }

}
