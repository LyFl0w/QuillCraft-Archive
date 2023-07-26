package net.quillcraft.core.manager;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.data.management.sql.DatabaseManager;
import org.lumy.api.LumyClient;

public class DataManager {

    public static void initAllData(QuillCraftCore quillCraftCore) {
        new LumyClient(quillCraftCore.getLogger(), quillCraftCore.getDataFolder());
        try {
            RedisManager.initAllRedisAccess();

            DatabaseManager.initAllDatabaseConnections();
            DatabaseManager.createAllTable();
        } catch(Exception exception) {
            quillCraftCore.getLogger().severe(exception.getMessage());
            quillCraftCore.getServer().shutdown();
        }
    }

    public static void closeAllData() {
        RedisManager.closeAllRedisAccess();
        DatabaseManager.closeAllDatabaseConnections();
    }

}
