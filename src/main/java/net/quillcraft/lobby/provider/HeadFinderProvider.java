package net.quillcraft.lobby.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.core.data.redis.RedisManager;
import net.quillcraft.core.data.sql.DatabaseManager;
import net.quillcraft.lobby.QuillCraftLobby;
import org.bukkit.entity.Player;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class HeadFinderProvider {

    private final static RedissonClient redissonClient = RedisManager.MESSAGE.getRedisAccess().getRedissonClient();
    private final String uuid, keyHeadList;
    private final List<Integer> headlist;

    public HeadFinderProvider(Player player) {
        uuid = player.getUniqueId().toString();
        keyHeadList = "headlist:"+uuid;
        this.headlist = getHeadList();
    }

    private List<Integer> getHeadList() {
        List<Integer> tempHeadlist = getHeadListFromRedis();
        if(tempHeadlist == null) {
            tempHeadlist = getHeadListFromDatabase();
            updateHeadListInRedis(tempHeadlist);
        }
        return tempHeadlist;
    }

    private List<Integer> getHeadListFromDatabase() {
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection(); //Ouverture de connection
            final PreparedStatement preparedStatementCheck = connection.prepareStatement("SELECT headlist FROM headfinder WHERE uuid = ?");// Précontruction d'une requète SQL
            preparedStatementCheck.setObject(1, uuid); // Finilisation de la requête
            preparedStatementCheck.executeQuery(); // Execute et récupere des données
            final ResultSet resultSet = preparedStatementCheck.getResultSet(); // Récupere les données de la commande
            final Gson gson = new GsonBuilder().serializeNulls().create();
            if(resultSet.next()) {
                String headlist = resultSet.getString("headlist");
                connection.close();
                if(headlist != null) {
                    return gson.fromJson(headlist, new TypeToken<ArrayList<Integer>>() {}.getType());
                } else {
                    return new ArrayList<>();
                }
            } else {
                connection.close();
                createHeadListInDatabase();
                return new ArrayList<>();
            }

        } catch(SQLException exception) {
            QuillCraftLobby.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return null;
    }

    private List<Integer> getHeadListFromRedis() {
        final RBucket<List<Integer>> headListRBucket = redissonClient.getBucket(keyHeadList);
        return headListRBucket.get();
    }

    private void createHeadListInDatabase() {
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection(); //Ouverture de connection
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO headfinder (uuid) VALUES (?)"); // Précontruction d'une requète SQ
            preparedStatement.setObject(1, uuid); // Finilisation de la requête
            preparedStatement.execute();    //Execution de la requete
            connection.close();
        } catch(SQLException exception) {
            QuillCraftLobby.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private void updateHeadListDatabase() {
        try {
            final Gson gson = new GsonBuilder().serializeNulls().create();
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE headfinder SET headlist = ? WHERE uuid = ?"); // Précontruction d'une requète SQL
            preparedStatement.setObject(1, gson.toJson(headlist)); // Finilisation de la requête / Serialise List<Integer> to String (-> Json)
            preparedStatement.setObject(2, uuid); // Finilisation de la requête
            preparedStatement.executeUpdate();    //Mise à jour de la liste dans la bdd
            connection.close();
        } catch(SQLException exception) {
            QuillCraftLobby.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private void updatePlayerCoins(Player player, int quillcoins) {
        try {
            final AccountProvider accountProvider = new AccountProvider(player);
            final Account account = accountProvider.getAccount();
            account.setQuillCoins(account.getQuillCoins()+quillcoins);
            accountProvider.updateAccount(account);
        } catch(Exception exception) {
            QuillCraftLobby.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private void updateHeadListInRedis(List<Integer> headlist) {
        final RBucket<List<Integer>> headListRBucket = redissonClient.getBucket(keyHeadList);
        headListRBucket.set(headlist);
    }

    public void updateHeadList(Player player, int quillcoins) {
        updateHeadListInRedis(headlist);
        updateHeadListDatabase();
        updatePlayerCoins(player, quillcoins);
    }

    public List<Integer> getHeadlist() {
        return headlist;
    }

}