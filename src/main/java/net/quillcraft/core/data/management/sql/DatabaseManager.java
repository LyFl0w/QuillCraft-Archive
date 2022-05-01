package net.quillcraft.core.data.management.sql;

import net.quillcraft.commons.game.GameEnum;
import net.quillcraft.commons.game.statistiques.PlayerGameStatistiqueProvider;
import net.quillcraft.core.manager.ConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public enum DatabaseManager {

    MINECRAFT_SERVER(new DatabaseCredentials(getConfiguration().getString("mysql.minecraft_data.host"),
            getConfiguration().getString("mysql.minecraft_data.user.name"), getConfiguration().getString("mysql.minecraft_data.user.password"),
            getConfiguration().getString("mysql.minecraft_data.database_name"), getConfiguration().getInt("mysql.minecraft_data.port"))),

    STATISTIQUES(new DatabaseCredentials(getConfiguration().getString("mysql.statistiques.host"),
            getConfiguration().getString("mysql.statistiques.user.name"), getConfiguration().getString("mysql.statistiques.user.password"),
            getConfiguration().getString("mysql.statistiques.database_name"), getConfiguration().getInt("mysql.statistiques.port")));

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

    public static void createAllTable(){
        Arrays.stream(GameEnum.values()).parallel().forEach(PlayerGameStatistiqueProvider::createTableIfNotExist);
    }

    private static FileConfiguration getConfiguration(){
        return ConfigurationManager.DATA_ACCESS.getConfiguration();
    }

}
