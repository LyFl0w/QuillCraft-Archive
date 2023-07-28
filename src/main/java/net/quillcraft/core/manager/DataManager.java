package net.quillcraft.core.manager;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.data.management.sql.DatabaseManager;
import org.lumy.api.LumyClient;

public class DataManager {

    private final QuillCraftCore quillCraftCore;
    private final LumyClient lumyClient;

    private String dataAccessPath;

    public DataManager(QuillCraftCore quillCraftCore) {
        this.quillCraftCore = quillCraftCore;

        lumyClient = new LumyClient(new String[]{"update", "absolute_path_data_access"}, quillCraftCore.getLogger(), quillCraftCore.getDataFolder());
        dataAccessPath = lumyClient.read();
    }

    public void init() {
        try {
            RedisManager.initAllRedisAccess();
            DatabaseManager.initAllDatabaseConnections();

            dataAccessPath = "";

            DatabaseManager.createAllTable();
        } catch(Exception exception) {
            quillCraftCore.getLogger().severe(exception.getMessage());
            quillCraftCore.getServer().shutdown();
        }
    }

    public void close() {
        RedisManager.closeAllRedisAccess();
        DatabaseManager.closeAllDatabaseConnections();
    }

    public LumyClient getLumyClient() {
        return lumyClient;
    }

    public String getDataAccessPath() {
        return dataAccessPath;
    }
}