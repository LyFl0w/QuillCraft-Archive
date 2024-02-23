package net.quillcraft.commons.game.statistiques;

import com.google.common.reflect.TypeToken;
import net.quillcraft.commons.game.GameEnum;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.redis.RedisManager;
import net.quillcraft.core.data.sql.DatabaseManager;
import net.quillcraft.core.serialization.ProfileSerializationType;
import org.bukkit.plugin.Plugin;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerGameStatistiqueProvider<T extends PlayerGameStatistique> {

    private static final RedissonClient redissonClient = RedisManager.STATISTIQUES.getRedisAccess().getRedissonClient();

    private final String gameName;
    private final String playerUUID;
    private final String key;
    private final T playerData;

    public PlayerGameStatistiqueProvider(Plugin plugin, UUID playerUUID, GameEnum gameEnum) {
        this.gameName = gameEnum.name().toLowerCase();
        this.playerUUID = playerUUID.toString();
        this.key = "STATS_" + gameName.toUpperCase() + ":".toUpperCase() + playerUUID;
        this.playerData = getPlayerDataBDD(plugin, (Class<T>) gameEnum.getGameStatistiqueClass());
    }

    public static void createTableIfNotExist(GameEnum gameEnum) {
        try (final Connection connection = DatabaseManager.STATISTIQUES.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "
                     + gameEnum.name().toLowerCase() + " ( uuid VARCHAR(36) NOT NULL , statistique JSON NULL DEFAULT NULL , UNIQUE (uuid))")) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    public T getPlayerData() {
        return playerData;
    }

    private T getPlayerDataBDD(Plugin plugin, Class<T> classOfT) {
        T tmpPlayerData = getPlayerDataFromRedis();
        if (tmpPlayerData == null) {
            tmpPlayerData = getPlayerDataFromDatabase(plugin, classOfT);
            updatePlayerDataInRedis(tmpPlayerData);
        } else {
            redissonClient.getBucket(key).clearExpire();
        }
        return tmpPlayerData;
    }

    public void updatePlayerData() {
        updatePlayerDataInRedis();
        updatePlayerDataInDatabase();
    }

    private void updatePlayerDataInRedis(T object) {
        final RBucket<T> stringRBucket = redissonClient.getBucket(key);
        stringRBucket.set(object);
    }

    private void updatePlayerDataInRedis() {
        updatePlayerDataInRedis(playerData);
    }

    private void updatePlayerDataInDatabase() {
        try (final Connection connection = DatabaseManager.STATISTIQUES.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + gameName + " SET statistique = ? WHERE uuid = ? ")) {
            preparedStatement.setString(1, new ProfileSerializationType().serialize(playerData));
            preparedStatement.setString(2, playerUUID);
            preparedStatement.execute();
        } catch (SQLException exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private T getPlayerDataFromRedis() {
        final RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    private T getPlayerDataFromDatabase(Plugin plugin, Class<T> classOfT) {
        try (final Connection connection = DatabaseManager.STATISTIQUES.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT statistique FROM " + gameName + " WHERE uuid = ?")) {

            preparedStatement.setString(1, playerUUID);
            final ResultSet resultSet = preparedStatement.executeQuery();
            final T tmpPlayerData;
            if (resultSet.next()) {
                tmpPlayerData = new ProfileSerializationType().deserialize(resultSet.getString("statistique"), TypeToken.of(classOfT));
                return tmpPlayerData;
            } else {
                tmpPlayerData = classOfT.getConstructor().newInstance();
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> createPlayerDataInDatabase(tmpPlayerData));
            }
            return tmpPlayerData;

        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }

        return null;
    }

    private void createPlayerDataInDatabase(T tmpPlayerData) {
        try (final Connection connection = DatabaseManager.STATISTIQUES.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + gameName + " (uuid, statistique) VALUES (?, ?)")) {
            preparedStatement.setString(1, playerUUID);
            preparedStatement.setString(2, new ProfileSerializationType().serialize(tmpPlayerData));
            preparedStatement.execute();
        } catch (SQLException exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    public void expireRedis() {
        redissonClient.getBucket(key).expire(Duration.ofMinutes(10));
    }

}
