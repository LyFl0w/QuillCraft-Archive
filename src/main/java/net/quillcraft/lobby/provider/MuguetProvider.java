package net.quillcraft.lobby.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.data.management.sql.DatabaseManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MuguetProvider {
    private final String uuid, keyMuguetCount;
    private final static RedissonClient redissonClient = RedisManager.MESSAGE.getRedisAccess().getRedissonClient();

    public MuguetProvider(Player player) {
        uuid = player.getUniqueId().toString();
        keyMuguetCount = "muguetCount:" + uuid;
    }


    public int getMuguetCount(){
        int tempMuguetCount = getMuguetCountFromRedis();
        if(tempMuguetCount == 0){
            tempMuguetCount = getMuguetCountFromDatabase();
            updateMuguetCountInRedis(tempMuguetCount);
        }
        return tempMuguetCount;
    }

    private int getMuguetCountFromDatabase(){
        try{
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection(); //Ouverture de connection
            final PreparedStatement preparedStatementCheck = connection.prepareStatement("SELECT muguetCounter FROM muguet WHERE uuid = ?");// Précontruction d'une requète SQL
            preparedStatementCheck.setObject(1, uuid); // Finilisation de la requête
            preparedStatementCheck.executeQuery(); // Execute et récupere des données
            final ResultSet resultSet = preparedStatementCheck.getResultSet(); // Récupere les données de la commande
            if(resultSet.next()){
                int muguetCount = resultSet.getInt("muguetCounter");
                connection.close();
                return muguetCount;
            }
            connection.close();
            createMuguetCountInDatabase();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    private void createMuguetCountInDatabase(){
        try{
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection(); //Ouverture de connection
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO muguet (uuid) VALUES (?)"); // Précontruction d'une requète SQ
            preparedStatement.setObject(1, uuid); // Finilisation de la requête
            preparedStatement.execute();    //Execution de la requete
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    private int getMuguetCountFromRedis(){
        final RAtomicLong muguetCountRBucket = redissonClient.getAtomicLong(keyMuguetCount);
        return (int) muguetCountRBucket.get();
    }

    private void updateMuguetCountDatabase(int muguetCount){
        try{
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE muguet SET muguetCounter = ? WHERE uuid = ?"); // Précontruction d'une requète SQL
            preparedStatement.setObject(1, muguetCount); // Finilisation de la requête / Serialise List<Integer> to String (-> Json)
            preparedStatement.setObject(2, uuid); // Finilisation de la requête
            preparedStatement.executeUpdate();    //Mise à jour de la liste dans la bdd
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void updateMuguetCountInRedis(int muguetCount){
        final RAtomicLong muguetCountRBucket = redissonClient.getAtomicLong(keyMuguetCount);
        muguetCountRBucket.set(muguetCount);
    }

    public void updateMuguetCounter(int muguetCount){
        updateMuguetCountInRedis(muguetCount);
        updateMuguetCountDatabase(muguetCount);
    }
}
