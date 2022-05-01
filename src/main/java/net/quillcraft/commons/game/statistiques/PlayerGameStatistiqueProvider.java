package net.quillcraft.commons.game.statistiques;

import com.google.common.reflect.TypeToken;
import net.quillcraft.commons.game.GameEnum;
import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.data.management.sql.DatabaseManager;
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

public class PlayerGameStatistiqueProvider<T extends PlayerGameStatistique>{

    private final static RedissonClient redissonClient = RedisManager.STATISTIQUES.getRedisAccess().getRedissonClient();

    private final String gameName, playerUUID, key;
    private final T playerData;

    public PlayerGameStatistiqueProvider(Plugin plugin, UUID playerUUID, GameEnum gameEnum){
        this.gameName = gameEnum.name().toLowerCase();
        this.playerUUID = playerUUID.toString();
        this.key = "STATS_"+gameName.toUpperCase()+":".toUpperCase()+playerUUID;
        this.playerData = getPlayerDataBDD(plugin, (Class<T>) gameEnum.getGameStatistiqueClass());
    }

    public T getPlayerData(){
        return playerData;
    }

    private T getPlayerDataBDD(Plugin plugin, Class<T> classOfT){
        T playerData = getPlayerDataFromRedis();
        if(playerData == null){
            playerData = getPlayerDataFromDatabase(plugin, classOfT);
            updatePlayerDataInRedis(playerData);
        }
        return playerData;
    }

    public void updatePlayerData(){
        updatePlayerDataInRedis();
        updatePlayerDataInDatabase();
    }

    private void updatePlayerDataInRedis(T object){
        final RBucket<T> stringRBucket = redissonClient.getBucket(key);
        stringRBucket.set(object);
    }

    private void updatePlayerDataInRedis(){
        updatePlayerDataInRedis(playerData);
    }

    private void updatePlayerDataInDatabase(){
        try{
            final Connection connection = DatabaseManager.STATISTIQUES.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE "+gameName+" SET statistique = ? WHERE uuid = ? ");
            preparedStatement.setString(1,  new ProfileSerializationType().serialize(playerData));
            preparedStatement.setString(2, playerUUID);
            preparedStatement.execute();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private T getPlayerDataFromRedis(){
        final RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    private T getPlayerDataFromDatabase(Plugin plugin, Class<T> classOfT){
        try{
            final Connection connection = DatabaseManager.STATISTIQUES.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT statistique FROM "+gameName+" WHERE uuid = ?");
            preparedStatement.setString(1, playerUUID);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                final T playerData = new ProfileSerializationType().deserialize(resultSet.getString("statistique"), new TypeToken<T>(){});
                connection.close();
                return playerData;
            }else{
                connection.close();
                final T playerData = classOfT.getConstructor().newInstance();
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> createPlayerDataInDatabase(playerData));
                return playerData;
            }
        }catch(SQLException|NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }

    private void createPlayerDataInDatabase(T playerData){
        createTableIfNotExist();
        try{
            final Connection connection = DatabaseManager.STATISTIQUES.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+gameName+" (uuid, statistique) VALUES (?, ?)");
            preparedStatement.setString(1, playerUUID);
            preparedStatement.setString(2, new ProfileSerializationType().serialize(playerData));
            preparedStatement.execute();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void createTableIfNotExist(){
        try{
            final Connection connection = DatabaseManager.STATISTIQUES.getDatabaseAccess().getConnection();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS "+gameName+" ( uuid VARCHAR(36) NOT NULL , statistique JSON NULL DEFAULT NULL , UNIQUE (uuid))").execute();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void expireRedis(){
        redissonClient.getBucket(key).expire(Duration.ofMinutes(5));
    }

}
