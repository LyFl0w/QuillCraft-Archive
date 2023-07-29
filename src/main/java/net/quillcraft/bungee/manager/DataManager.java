package net.quillcraft.bungee.manager;

import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import net.quillcraft.bungee.data.management.sql.DatabaseManager;

import org.lumy.api.LumyClient;

public class DataManager {

    private final QuillCraftBungee quillCraftBungee;
    private final LumyClient lumyClient;

    private String dataAccessPath;

    public DataManager(QuillCraftBungee quillCraftBungee) {
        this.quillCraftBungee = quillCraftBungee;

        lumyClient = new LumyClient(new String[]{"update", "absolute_path_data_access"}, quillCraftBungee.getLogger(), quillCraftBungee.getDataFolder());
        dataAccessPath = lumyClient.read();
    }

    public void init() {
        try {
            RedisManager.initAllRedisAccess();
            DatabaseManager.initAllDatabaseConnections();

            dataAccessPath = "";
        } catch(Exception exception) {
            quillCraftBungee.getLogger().severe(exception.getMessage());
            quillCraftBungee.getProxy().stop();
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
