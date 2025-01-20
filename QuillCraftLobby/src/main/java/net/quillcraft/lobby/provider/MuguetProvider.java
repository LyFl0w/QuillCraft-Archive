package net.quillcraft.lobby.provider;

import net.quillcraft.core.data.redis.RedisManager;
import net.quillcraft.core.data.sql.DatabaseManager;
import net.quillcraft.lobby.QuillCraftLobby;
import org.bukkit.entity.Player;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MuguetProvider {

    private static final RedissonClient redissonClient = RedisManager.MESSAGE.getRedisAccess().getRedissonClient();
    private final String uuid;
    private final String name;
    private final String keyMuguetCount;

    public MuguetProvider(Player player) {
        name = player.getName();
        uuid = player.getUniqueId().toString();
        keyMuguetCount = "muguetCount:"+uuid;
    }

    public static List<String> getTop(int maxTop) {
        final ArrayList<String> playersName = new ArrayList<>();
        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM muguet ORDER BY muguetCounter DESC LIMIT ?")) {

            preparedStatement.setInt(1, maxTop);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    playersName.add(resultSet.getString("name"));
                }
            }
        } catch (SQLException exception) {
            QuillCraftLobby.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return playersName;
    }

    public static int getSum() {
        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(muguetCounter) as sum from muguet");
             final ResultSet resultSet = preparedStatement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt("sum");
        } catch (SQLException exception) {
            QuillCraftLobby.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return 0;
    }

    public int getMuguetCount() {
        int tempMuguetCount = getMuguetCountFromRedis();
        if(tempMuguetCount == 0) {
            tempMuguetCount = getMuguetCountFromDatabase();
            updateMuguetCountInRedis(tempMuguetCount);
        }
        return tempMuguetCount;
    }

    private int getMuguetCountFromDatabase() {
        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT muguetCounter FROM muguet WHERE uuid = ?")) {

            preparedStatement.setObject(1, uuid);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return resultSet.getInt("muguetCounter");
            }
        } catch (SQLException exception) {
            QuillCraftLobby.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }

        createMuguetCountInDatabase();
        return 0;
    }

    private void createMuguetCountInDatabase() {
        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO muguet (uuid, name) VALUES (?,?)")) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (SQLException exception) {
            QuillCraftLobby.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private int getMuguetCountFromRedis() {
        final RAtomicLong muguetCountRBucket = redissonClient.getAtomicLong(keyMuguetCount);
        return (int) muguetCountRBucket.get();
    }

    private void updateMuguetCountDatabase(int muguetCount) {
        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE muguet SET muguetCounter = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, muguetCount);
            preparedStatement.setString(2, uuid);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            QuillCraftLobby.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private void updateMuguetCountInRedis(int muguetCount) {
        final RAtomicLong muguetCountRBucket = redissonClient.getAtomicLong(keyMuguetCount);
        muguetCountRBucket.set(muguetCount);
    }

    public void updateMuguetCounter(int muguetCount) {
        updateMuguetCountInRedis(muguetCount);
        updateMuguetCountDatabase(muguetCount);
    }


}
