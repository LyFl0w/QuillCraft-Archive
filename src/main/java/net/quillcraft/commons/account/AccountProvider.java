package net.quillcraft.commons.account;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import net.quillcraft.bungee.data.management.sql.DatabaseManager;
import net.quillcraft.bungee.data.management.sql.table.SQLTablesManager;
import net.quillcraft.bungee.serialization.ProfileSerializationAccount;
import net.quillcraft.commons.exception.AccountNotFoundException;

import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AccountProvider {

    private final String keyAccount;
    private final RedissonClient redissonClient;
    private final UUID uuid;
    private final SQLTablesManager sqlTablesManager;

    public AccountProvider(UUID uuid){
        this.uuid = uuid;
        this.redissonClient = RedisManager.ACCOUNT.getRedisAccess().getRedissonClient();
        this.keyAccount = "account:"+uuid.toString();
        this.sqlTablesManager = SQLTablesManager.PLAYER_ACCOUNT;
    }

    public AccountProvider(ProxiedPlayer player){
        this(player.getUniqueId());
    }

    public Account getAccount() throws AccountNotFoundException{
        Account account = getAccountFromRedis();

        if(account == null){
            account = getAccountFromDatabase();
            sendAccountToRedis(account);
        }else{
            account.setSQLRequest();
            redissonClient.getBucket(keyAccount).clearExpire();
        }

        return account;
    }

    public Account getAccountFromRedis(){
        final RBucket<Account> accountRBucket = redissonClient.getBucket(keyAccount);

        return accountRBucket.get();
    }

    public void updateAccount(final Account account){
        try{
            sendAccountToRedis(account);
            updateAccountDatabase(account.getSQLRequest().getUpdateRequest(DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection()));
        }catch(Exception exception){
            exception.printStackTrace();
        }

    }

    private void sendAccountToRedis(final Account account){
        final RBucket<Account> accountRBucket = redissonClient.getBucket(keyAccount);

        accountRBucket.set(account);
    }

    public Account getAccountFromDatabase() throws AccountNotFoundException{
        try{
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+sqlTablesManager.getTable()+" WHERE "+sqlTablesManager.getKeyColumn()+" = ?");

            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();
            if(resultSet.next()){
                final int id = resultSet.getInt("id");
                final String partyUUID = resultSet.getString("party_uuid");
                final int quillCoin = resultSet.getInt("quillcoins");
                final byte rankID = resultSet.getByte("rank_id");
                final Account.Visibility visibility = Account.Visibility.valueOf(resultSet.getString("visibility"));
                final HashMap<Account.Particles, Boolean> particule = new ProfileSerializationAccount.Particle().deserialize(resultSet.getString("json_particles"));
                final String languageISO = resultSet.getString("language");

                connection.close();
                return new Account(id, uuid, ((partyUUID != null) ? UUID.fromString(partyUUID) : null),
                        quillCoin, rankID, visibility, particule, languageISO);
            }else{
                connection.close();
                return createAccountInDatabase();
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        throw new AccountNotFoundException(uuid);
    }

    public void updateAccountDatabase(final PreparedStatement preparedStatement){
        try{
            preparedStatement.executeUpdate();
            preparedStatement.getConnection().close();
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    private Account createAccountInDatabase() throws SQLException{
        final Account account = new Account(uuid);
        final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
        //TODO:GET ID FIRST OF ALL !!!!
        final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+sqlTablesManager.getTable()+" (uuid, quillcoins, json_particles) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, uuid.toString());
        preparedStatement.setInt(2, account.getQuillCoin());
        preparedStatement.setString(3, new ProfileSerializationAccount.Particle().serialize(account.getParticles()));
        final int row = preparedStatement.executeUpdate();
        final ResultSet resultSet = preparedStatement.getGeneratedKeys();
        //TODO:GET ID
        if(row > 0 && resultSet.next()){
            account.setId(resultSet.getInt(1));
        }
        connection.close();

        autoLangue();

        return account;
    }

    public void expireRedis(){
        redissonClient.getBucket(keyAccount).expire(6, TimeUnit.HOURS);
    }

    private void autoLangue(){
        final RSet<Integer> updateLanguage = redissonClient.getSet("autoLanguage:"+uuid.toString());
        updateLanguage.add(0);
        updateLanguage.expire(10, TimeUnit.SECONDS);
    }

}