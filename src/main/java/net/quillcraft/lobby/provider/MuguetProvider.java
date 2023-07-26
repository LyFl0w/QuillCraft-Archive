package net.quillcraft.lobby.provider;

import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.data.management.sql.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MuguetProvider {

    private final static RedissonClient redissonClient = RedisManager.MESSAGE.getRedisAccess().getRedissonClient();
    private final String uuid, name, keyMuguetCount;

    public MuguetProvider(Player player) {
        name = player.getName();
        uuid = player.getUniqueId().toString();
        keyMuguetCount = "muguetCount:"+uuid;
    }

    public static List<String> getTop(int maxTop) {
        final ArrayList<String> playersName = new ArrayList<>();
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM muguet ORDER BY muguetCounter DESC LIMIT ?"); // Précontruction d'une requète SQL
            preparedStatement.setInt(1, maxTop); // Finilisation de la requête
            final ResultSet resultSet = preparedStatement.executeQuery();    // Recuperation des données

            while(resultSet.next()) {
                playersName.add(resultSet.getString("name"));
            }

            connection.close();
        } catch(SQLException exception) {
            Bukkit.getLogger().severe(exception.getMessage());
        }
        return playersName;
    }

    public static int getSum() {
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(muguetCounter) as sum from muguet"); // Précontruction d'une requète SQL
            final ResultSet resultSet = preparedStatement.executeQuery();    // Recuperation des données

            resultSet.next();
            final int sum = resultSet.getInt("sum");
            connection.close();

            return sum;
        } catch(SQLException exception) {
            Bukkit.getLogger().severe(exception.getMessage());
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
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection(); //Ouverture de connection
            final PreparedStatement preparedStatementCheck = connection.prepareStatement("SELECT muguetCounter FROM muguet WHERE uuid = ?");// Précontruction d'une requète SQL
            preparedStatementCheck.setObject(1, uuid); // Finilisation de la requête
            preparedStatementCheck.executeQuery(); // Execute et récupere des données
            final ResultSet resultSet = preparedStatementCheck.getResultSet(); // Récupere les données de la commande
            if(resultSet.next()) {
                int muguetCount = resultSet.getInt("muguetCounter");
                connection.close();
                return muguetCount;
            }
            connection.close();
            createMuguetCountInDatabase();
        } catch(SQLException exception) {
            Bukkit.getLogger().severe(exception.getMessage());
        }
        return 0;
    }

    private void createMuguetCountInDatabase() {
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection(); //Ouverture de connection
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO muguet (uuid, name) VALUES (?,?)"); // Précontruction d'une requète SQ
            preparedStatement.setString(1, uuid); // Finilisation de la requête
            preparedStatement.setString(2, name); // Finilisation de la requête
            preparedStatement.execute();    //Execution de la requete
            connection.close();
        } catch(SQLException exception) {
            Bukkit.getLogger().severe(exception.getMessage());
        }
    }

    private int getMuguetCountFromRedis() {
        final RAtomicLong muguetCountRBucket = redissonClient.getAtomicLong(keyMuguetCount);
        return (int) muguetCountRBucket.get();
    }

    private void updateMuguetCountDatabase(int muguetCount) {
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE muguet SET muguetCounter = ? WHERE uuid = ?"); // Précontruction d'une requète SQL
            preparedStatement.setInt(1, muguetCount); // Finilisation de la requête
            preparedStatement.setString(2, uuid); // Finilisation de la requête
            preparedStatement.executeUpdate();    //Mise à jour de la liste dans la bdd
            connection.close();
        } catch(SQLException exception) {
            Bukkit.getLogger().severe(exception.getMessage());
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
