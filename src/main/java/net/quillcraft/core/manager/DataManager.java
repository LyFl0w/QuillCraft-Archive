package net.quillcraft.core.manager;

import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.data.management.sql.DatabaseManager;
import org.bukkit.Bukkit;

public class DataManager {

    public static void initAllData(){
        try{
            RedisManager.initAllRedisAccess();
            DatabaseManager.initAllDatabaseConnections();
        }catch(Exception e){
            e.printStackTrace();
            Bukkit.shutdown();
        }
    }

    public static void closeAllData(){
        RedisManager.closeAllRedisAccess();
        DatabaseManager.closeAllDatabaseConnections();
    }

}
