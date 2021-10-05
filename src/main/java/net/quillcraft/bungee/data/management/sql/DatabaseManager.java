package net.quillcraft.bungee.data.management.sql;

import net.md_5.bungee.config.Configuration;
import net.quillcraft.bungee.manager.ConfigurationManager;

public enum DatabaseManager {

    MINECRAFT_SERVER(new DatabaseCredentials(getConfiguration().getString("mysql.minecraft_server.host"),
            getConfiguration().getString("mysql.minecraft_server.user.name"), getConfiguration().getString("mysql.minecraft_server.user.password"),
            getConfiguration().getString("mysql.minecraft_server.database_name"), getConfiguration().getInt("mysql.minecraft_server.port")));

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

    private static Configuration getConfiguration(){
        return ConfigurationManager.DATA_ACCESS.getConfiguration();
    }

}
