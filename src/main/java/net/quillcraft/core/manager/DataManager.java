package net.quillcraft.core.manager;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.data.management.sql.DatabaseManager;
import org.lumy.api.LumyClient;

public class DataManager {

    public static void initAllData(QuillCraftCore quillCraftCore) {
        final LumyClient lumyClient = new LumyClient(new String[]{"update", "absolute_path_data_access"},
                quillCraftCore.getLogger(), quillCraftCore.getDataFolder());

        quillCraftCore.data_access_path = lumyClient.read();

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
