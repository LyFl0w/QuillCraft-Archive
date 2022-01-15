package net.quillcraft.core.manager;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.data.management.sql.DatabaseManager;

import org.bukkit.Bukkit;
import org.lumy.api.LumyClient;

public class DataManager {

    public static void initAllData(QuillCraftCore quillCraftCore){
        new LumyClient(quillCraftCore.getLogger(), quillCraftCore.getDataFolder());
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
