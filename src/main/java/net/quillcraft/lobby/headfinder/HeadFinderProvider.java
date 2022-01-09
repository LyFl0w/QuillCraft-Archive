package net.quillcraft.lobby.headfinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.data.management.sql.DatabaseManager;
import org.bukkit.entity.Player;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HeadFinderProvider {
    private final String uuid, keyHeadList;
    private List<Integer> headlist;
    private final RedissonClient redissonClient;


    public HeadFinderProvider(Player player) {
         uuid = player.getUniqueId().toString();
         headlist = getHeadList();
         keyHeadList = "headlist:"+uuid;
        this.redissonClient = RedisManager.PLAYER_DATA.getRedisAccess().getRedissonClient();
    }

    private List<Integer> getHeadList(){
       headlist = getHeadListFromRedis();
       if(headlist == null){
           headlist = getHeadListFromDatabase();
           updateHeadListInRedis(headlist);
       }
       return headlist;
    }

    private List<Integer> getHeadListFromDatabase(){
        try{
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection(); //Ouverture de connection
            final PreparedStatement preparedStatementCheck = connection.prepareStatement("SELECT headlist FROM headfinder WHERE uuid = ?");// Précontruction d'une requète SQL
            preparedStatementCheck.setObject(1,uuid); // Finilisation de la requête
            preparedStatementCheck.executeQuery(); // Execute et récupere des données
            final ResultSet resultSet = preparedStatementCheck.getResultSet(); // Récupere les données de la commande
            connection.close();
            final Gson gson = new GsonBuilder().serializeNulls().create();
            if(resultSet.next()){
                String headlist = resultSet.getString("headlist");
                if(headlist != null) {
                    return gson.fromJson(headlist, new TypeToken<ArrayList<Integer>>(){}.getType());
                }else{
                    return new ArrayList<>();
                }
            }else{
                createHeadListInDatabase();
                return new ArrayList<>();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Integer> getHeadListFromRedis(){
        return null;
    }

    private void createHeadListInDatabase(){
        try{
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection(); //Ouverture de connection
            final PreparedStatement preparedStatement =  connection.prepareStatement("INSERT INTO headfinder (uuid) VALUES (?)"); // Précontruction d'une requète SQ
            preparedStatement.setObject(1, uuid); // Finilisation de la requête
            preparedStatement.execute();    //Execution de la requete
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateHeadListDatabase(){
        try{
            final Gson gson = new GsonBuilder().serializeNulls().create();
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement =  connection.prepareStatement("UPDATE headfinder SET headlist = ? WHERE uuid = ?"); // Précontruction d'une requète SQL
            preparedStatement.setObject(1, gson.toJson(headlist)); // Finilisation de la requête / Serialise List<Integer> to String (-> Json)
            preparedStatement.setObject(2, uuid); // Finilisation de la requête
            preparedStatement.executeUpdate();    //Mise à jour de la liste dans la bdd
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateHeadList(){
        updateHeadListInRedis(headlist);
        updateHeadListDatabase();

    }

    private void updateHeadListInRedis(List<Integer> headlist){
        final RBucket<List<Integer>> headListRBucket = redissonClient.getBucket(keyHeadList);
        headListRBucket.set(headlist);
    }

    public List<Integer> getHeadlist() {
        return headlist;
    }
    public List<Integer> getHeadListInRedis(){
        final RBucket<List<Integer>> headListRBucket = redissonClient.getBucket(keyHeadList);
        return headListRBucket.get();
    }
}
