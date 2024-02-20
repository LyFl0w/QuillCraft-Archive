package net.quillcraft.commons.account;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.data.redis.RedisManager;
import net.quillcraft.bungee.data.sql.DatabaseManager;
import net.quillcraft.bungee.data.sql.table.SQLTablesManager;
import net.quillcraft.bungee.serialization.ProfileSerializationAccount;
import net.quillcraft.commons.exception.AccountNotFoundException;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.sql.*;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class AccountProvider {

    private final String keyAccount;
    private final RedissonClient redissonClient;
    private final UUID uuid;
    private final SQLTablesManager sqlTablesManager;

    public AccountProvider(UUID uuid) {
        this.uuid = uuid;
        this.redissonClient = RedisManager.ACCOUNT.getRedisAccess().getRedissonClient();
        this.keyAccount = "account:"+uuid.toString();
        this.sqlTablesManager = SQLTablesManager.PLAYER_ACCOUNT;
    }

    public AccountProvider(ProxiedPlayer player) {
        this(player.getUniqueId());
    }

    public Account getAccount() throws AccountNotFoundException {
        Account account = getAccountFromRedis();

        if(account == null) {
            account = getAccountFromDatabase();
            sendAccountToRedis(account);
        } else {
            account.setSQLRequest();
            redissonClient.getBucket(keyAccount).clearExpire();
        }

        return account;
    }

    public Account getAccountFromRedis() {
        final RBucket<Account> accountRBucket = redissonClient.getBucket(keyAccount);

        return accountRBucket.get();
    }

    public void updateAccount(final Account account) {
        try {
            sendAccountToRedis(account);
            updateAccountDatabase(account.getSQLRequest().getUpdateRequest(DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection()));
        } catch(Exception exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }

    }

    private void sendAccountToRedis(final Account account) {
        final RBucket<Account> accountRBucket = redissonClient.getBucket(keyAccount);

        accountRBucket.set(account);
    }

    public Account getAccountFromDatabase() throws AccountNotFoundException {

        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + sqlTablesManager.getTable() + " WHERE " + sqlTablesManager.getKeyColumn() + " = ?")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeQuery();

            try (final ResultSet resultSet = preparedStatement.getResultSet()) {
                if (resultSet.next()) {
                    final int id = resultSet.getInt("id");
                    final String partyUUID = resultSet.getString("party_uuid");
                    final int quillCoin = resultSet.getInt("quillcoins");
                    final byte rankID = resultSet.getByte("rank_id");
                    final Account.Visibility visibility = Account.Visibility.valueOf(resultSet.getString("visibility"));
                    final Map<Account.Particles, Boolean> particule = new ProfileSerializationAccount.Particle().deserialize(resultSet.getString("json_particles"));
                    final String languageISO = resultSet.getString("language");

                    return new Account(id, uuid, ((partyUUID != null) ? UUID.fromString(partyUUID) : null), quillCoin, rankID, visibility, particule, languageISO);
                } else {
                    return createAccountInDatabase();
                }
            }
        } catch (SQLException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }

        throw new AccountNotFoundException(uuid);
    }

    public void updateAccountDatabase(final PreparedStatement preparedStatement) {
        try (preparedStatement) {
            preparedStatement.executeUpdate();
            preparedStatement.getConnection().close();
        } catch(Exception exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private Account createAccountInDatabase() throws SQLException {
        final Account account = new Account(uuid);
        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+sqlTablesManager.getTable()+" (uuid, quillcoins, json_particles) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, account.getQuillCoin());
            preparedStatement.setString(3, new ProfileSerializationAccount.Particle().serialize(account.getParticles()));

            // GET ID
            final int row = preparedStatement.executeUpdate();
            try (final ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if(row > 0 && resultSet.next())
                    account.setId(resultSet.getInt(1));
            }
        }

        autoLangue();

        return account;
    }

    public void expireRedis() {
        redissonClient.getBucket(keyAccount).expire(Duration.ofHours(4));
    }

    private void autoLangue() {
        final RSet<Integer> updateLanguage = redissonClient.getSet("autoLanguage:"+uuid.toString());
        updateLanguage.add(0);
        updateLanguage.expire(Duration.ofSeconds(10));
    }

}