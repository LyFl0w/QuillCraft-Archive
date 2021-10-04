package net.quillcraft.commons.account;

import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.management.redis.RedisManager;
import net.quillcraft.core.data.management.sql.DatabaseManager;
import net.quillcraft.core.event.player.PlayerChangeLanguageEvent;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.manager.ProfileSerializationManager;
import net.quillcraft.core.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AccountProvider {

    private final String keyAccount, keyAutoLanguage, keyUpdateLanguage, keyUpdateVisibility;
    private final RedissonClient redissonClient;
    private final Player player;
    private final UUID uuid;

    public AccountProvider(Player player){
        this.player = player;
        this.uuid = player.getUniqueId();
        this.redissonClient = RedisManager.PLAYER_DATA.getRedisAccess().getRedissonClient();
        final String uuidString = uuid.toString();
        this.keyAccount = "account:"+uuidString;
        this.keyAutoLanguage = "autoLanguage:"+uuidString;
        this.keyUpdateLanguage = "updateLanguage:"+uuidString;
        this.keyUpdateVisibility = "updateVisibility:"+uuidString;
    }

    public final Account getAccount() throws AccountNotFoundException{
        Account account = getAccountFromRedis();

        if(account == null){
            account = getAccountFromDatabase();
            sendAccountToRedis(account);
        }

        return account;
    }

    private Account getAccountFromRedis(){
        final RBucket<Account> accountRBucket = redissonClient.getBucket(keyAccount);

        return accountRBucket.get();
    }

    public void updateAccount(final Account account){
        PreparedStatement updateRequest = null;
        try{
            updateRequest = account.getSQLRequest().getUpdateRequest(DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection());
            System.out.println(account.getSQLRequest());
        }catch(Exception exception){
            exception.printStackTrace();
        }
        sendAccountToRedis(account);
        sendAccountToDatabase(updateRequest);
    }

    private void sendAccountToRedis(final Account account){
        final RBucket<Account> accountRBucket = redissonClient.getBucket(keyAccount);
        accountRBucket.set(account);
    }

    private Account getAccountFromDatabase() throws AccountNotFoundException{
        try{
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM playerdata WHERE uuid = ?");

            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();
            if(resultSet.next()){
                player.sendMessage(ChatColor.GREEN+"Votre compte a bien été trouvé !");

                final int id = resultSet.getInt("id");
                final String partyUUID = resultSet.getString("partyuuid");
                final int quillCoins = resultSet.getInt("quillcoins");
                final byte rankID = resultSet.getByte("rankid");
                final Account.Visibility visibility = Account.Visibility.valueOf(resultSet.getString("visibility"));
                final HashMap<Account.Particles, Boolean> particules = new ProfileSerializationManager().deserializeParticle(resultSet.getString("jsonparticles"));
                final String languageISO = resultSet.getString("language");

                connection.close();

                Account account;
                account = new Account(id, uuid, quillCoins, rankID, visibility, particules, languageISO);
                if(partyUUID != null){
                    account = new Account(id, uuid, UUID.fromString(partyUUID), quillCoins, rankID, visibility, particules, languageISO);
                }

                return account;
            }else{
                connection.close();
                return createNewAccount();
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        throw new AccountNotFoundException(player);
    }

    private void sendAccountToDatabase(final PreparedStatement updateRequest){
        try{
            updateRequest.executeUpdate();
            updateRequest.getConnection().close();
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    private Account createNewAccount() throws SQLException{
        final Account account = new Account(player);
        final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();

        final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO playerdata (uuid, quillcoins, jsonparticles) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, uuid.toString());
        preparedStatement.setInt(2, account.getQuillCoins());
        preparedStatement.setString(3, new ProfileSerializationManager().serialize(account.getParticles()));

        final int row = preparedStatement.executeUpdate();
        final ResultSet resultSet = preparedStatement.getGeneratedKeys();

        //GET ID
        if(row > 0 && resultSet.next()){
            account.setId(resultSet.getInt(1));
        }
        connection.close();

        cooldownLanguage();

        return account;
    }

    public void setLocaleLanguage(final Account account){
        player.sendMessage(LanguageManager.getMessageByDefaultLanguage(Text.COMMAND_SETLANGUAGE_AUTO));
        QuillCraftCore.getInstance().getServer().getPluginManager().callEvent(new PlayerChangeLanguageEvent(player, this, account, player.getLocale()));
    }

    private void cooldownLanguage(){
        updateKey(keyAutoLanguage);
    }

    public boolean hasAutoLanguage(){
        return redissonClient.getSet(keyAutoLanguage).delete();
    }

    public void updateLanguage(){
        updateKey(keyUpdateLanguage);
    }

    public boolean hasUpdatedLanguage(){
        return keyExist(keyUpdateLanguage);
    }

    public void updateVisibility(){
        updateKey(keyUpdateVisibility);
    }

    public boolean hasUpdatedVisibility(){
        return keyExist(keyUpdateVisibility);
    }

    private void updateKey(final String key){
        final RSet<Integer> updateLanguage = redissonClient.getSet(key);
        updateLanguage.add(0);
        updateLanguage.expire(10, TimeUnit.SECONDS);
    }

    private boolean keyExist(final String key){
        return redissonClient.getSet(key).isExists();
    }

}