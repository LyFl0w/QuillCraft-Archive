package net.quillcraft.bungee.manager;

import net.md_5.bungee.api.ProxyServer;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import net.quillcraft.bungee.data.management.sql.DatabaseManager;
import org.lumy.api.LumyClient;

public class DataManager {

    public static void initAllData(QuillCraftBungee quillCraftBungee){

        new LumyClient(quillCraftBungee.getLogger(), quillCraftBungee.getDataFolder());
        try{
            RedisManager.initAllRedisAccess();
            DatabaseManager.initAllDatabaseConnections();
        }catch(Exception e){
            e.printStackTrace();
            ProxyServer.getInstance().stop();
        }
    }

    public static void closeAllData(){
        RedisManager.closeAllRedisAccess();
        DatabaseManager.closeAllDatabaseConnections();
    }

}
