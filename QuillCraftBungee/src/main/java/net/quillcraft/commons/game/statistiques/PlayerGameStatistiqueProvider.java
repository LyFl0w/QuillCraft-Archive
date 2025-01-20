package net.quillcraft.commons.game.statistiques;

import com.google.common.reflect.TypeToken;
import net.md_5.bungee.api.plugin.Plugin;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.data.redis.RedisManager;
import net.quillcraft.bungee.data.sql.DatabaseManager;
import net.quillcraft.bungee.serialization.ProfileSerializationType;
import net.quillcraft.commons.game.GameEnum;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerGameStatistiqueProvider<T extends PlayerGameStatistique> {

    private static final RedissonClient redissonClient = RedisManager.STATISTIQUES.getRedisAccess().getRedissonClient();

    private final String gameName;
    private final String playerUUID;
    private final String key;
    private final T playerData;

    public PlayerGameStatistiqueProvider(Plugin plugin, UUID playerUUID, GameEnum gameEnum) {
        this.gameName = gameEnum.name();
        this.playerUUID = playerUUID.toString();
        this.key = "STATS_"+gameName+":".toUpperCase()+playerUUID;
        this.playerData = getPlayerDataBDD(plugin, (Class<T>) gameEnum.getGameStatistiqueClass());
    }

    public T getPlayerData() {
        return playerData;
    }

    private T getPlayerDataBDD(Plugin plugin, Class<T> classOfT) {
        T tmpPlayerData = getPlayerDataFromRedis();
        if(tmpPlayerData == null) {
            tmpPlayerData = getPlayerDataFromDatabase(plugin, classOfT);
            updatePlayerDataInRedis(tmpPlayerData);
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
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + gameName.toLowerCase() + " SET statistique = ? WHERE uuid = ? ")) {
            preparedStatement.setString(1, new ProfileSerializationType().serialize(playerData));
            preparedStatement.setString(2, playerUUID);
            preparedStatement.execute();
        } catch (SQLException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
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
                tmpPlayerData = new ProfileSerializationType().deserialize(resultSet.getString("statistique"), new TypeToken<>() {
                });
            } else {
                tmpPlayerData = classOfT.getConstructor().newInstance();
                plugin.getProxy().getScheduler().runAsync(plugin, () -> createPlayerDataInDatabase(tmpPlayerData));
            }
            return tmpPlayerData;

        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return null;
    }

    private void createPlayerDataInDatabase(T playerData) {
        createTableIfNotExist();
        try (final Connection connection = DatabaseManager.STATISTIQUES.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + gameName + " (uuid, statistique) VALUES (?, ?)")) {
            preparedStatement.setString(1, playerUUID);
            preparedStatement.setString(2, new ProfileSerializationType().serialize(playerData));
            preparedStatement.execute();
        } catch (SQLException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private void createTableIfNotExist() {
        try (final Connection connection = DatabaseManager.STATISTIQUES.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "
                     + gameName + " ( uuid VARCHAR(36) NOT NULL , statistique JSON NULL DEFAULT NULL , UNIQUE (uuid))")) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

}
