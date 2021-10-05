package net.quillcraft.bungee.manager;

import net.md_5.bungee.BungeeCord;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import net.quillcraft.bungee.data.management.sql.DatabaseManager;

public class DataManager {

    public static void initAllData(){
        try{
            RedisManager.initAllRedisAccess();
            DatabaseManager.initAllDatabaseConnections();
        }catch(Exception e){
            e.printStackTrace();
            BungeeCord.getInstance().stop();
        }
    }

    public static void closeAllData(){
        RedisManager.closeAllRedisAccess();
        DatabaseManager.closeAllDatabaseConnections();
    }

}
