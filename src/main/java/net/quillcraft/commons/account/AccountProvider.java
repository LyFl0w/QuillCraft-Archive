package net.quillcraft.commons.account;

import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.redis.RedisManager;
import net.quillcraft.core.data.sql.DatabaseManager;
import net.quillcraft.core.data.sql.table.SQLTablesManager;
import net.quillcraft.core.event.player.PlayerChangeLanguageEvent;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.serialization.ProfileSerializationAccount;
import net.quillcraft.lumy.api.text.Text;
import org.bukkit.entity.Player;
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
    private final String keyAutoLanguage;
    private final String keyUpdateLanguage;
    private final String keyUpdateVisibility;
    private final RedissonClient redissonClient;
    private final Player player;
    private final UUID uuid;
    private final SQLTablesManager sqlTablesManager;

    public AccountProvider(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.redissonClient = RedisManager.ACCOUNT.getRedisAccess().getRedissonClient();
        this.keyAccount = "account:" + uuid;
        this.keyAutoLanguage = "autoLanguage:" + uuid;
        this.keyUpdateLanguage = "updateLanguage:" + uuid;
        this.keyUpdateVisibility = "updateVisibility:" + uuid;
        this.sqlTablesManager = SQLTablesManager.PLAYER_ACCOUNT;
    }

    public final Account getAccount() throws AccountNotFoundException {
        Account account = getAccountFromRedis();

        if (account == null) {
            account = getAccountFromDatabase();
            sendAccountToRedis(account);
        } else {
            account.setSQLRequest();
        }

        return account;
    }

    private Account getAccountFromRedis() {
        final RBucket<Account> accountRBucket = redissonClient.getBucket(keyAccount);

        return accountRBucket.get();
    }

    public void updateAccount(final Account account) {
        try (final PreparedStatement updateRequest =
                     account.getSQLRequest().getUpdateRequest(DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection())) {
            sendAccountToRedis(account);
            sendAccountToDatabase(updateRequest);
        } catch (Exception exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private void sendAccountToRedis(final Account account) {
        final RBucket<Account> accountRBucket = redissonClient.getBucket(keyAccount);
        accountRBucket.set(account);
    }

    private Account getAccountFromDatabase() throws AccountNotFoundException {
        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement =
                     connection.prepareStatement("SELECT * FROM " + sqlTablesManager.getTable() + " WHERE " + sqlTablesManager.getKeyColumn() + " = ?")) {

            preparedStatement.setString(1, uuid.toString());
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final int id = resultSet.getInt("id");
                final String partyUUID = resultSet.getString("party_uuid");
                final int quillCoins = resultSet.getInt("quillcoins");
                final byte rankID = resultSet.getByte("rank_id");
                final Account.Visibility visibility = Account.Visibility.valueOf(resultSet.getString("visibility"));

                final Map<Account.Particles, Boolean> particules = new ProfileSerializationAccount.Particle().deserialize(resultSet.getString("json_particles"));
                final String languageISO = resultSet.getString("language");

                return new Account(id, uuid, (partyUUID != null ? UUID.fromString(partyUUID) : null), quillCoins, rankID, visibility, particules, languageISO);
            } else {
                return createNewAccount();
            }

        } catch (SQLException exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }

        throw new AccountNotFoundException(uuid);
    }

    private void sendAccountToDatabase(final PreparedStatement updateRequest) {
        try {
            updateRequest.executeUpdate();
            updateRequest.getConnection().close();
        } catch (Exception exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private Account createNewAccount() throws SQLException {
        final Account account = new Account(player);
        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement =
                     connection.prepareStatement("INSERT INTO " + sqlTablesManager.getTable() + " (uuid, quillcoins, json_particles) VALUES (?, ?, ?)",
                             Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setInt(2, account.getQuillCoins());
            preparedStatement.setString(3, new ProfileSerializationAccount.Particle().serialize(account.getParticles()));

            //GET ID
            final int row = preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (row > 0 && resultSet.next()) {
                account.setId(resultSet.getInt(1));
            }

            cooldownLanguage();

            return account;
        }
    }

    public void setLocaleLanguage(final Account account) {
        player.sendMessage(LanguageManager.DEFAULT.getMessage(Text.COMMAND_SETLANGUAGE_AUTO));
        QuillCraftCore.getInstance().getServer().getPluginManager().callEvent(new PlayerChangeLanguageEvent(player, this, account, player.getLocale()));
    }

    private void cooldownLanguage() {
        updateKey(keyAutoLanguage);
    }

    public boolean hasAutoLanguage() {
        return redissonClient.getSet(keyAutoLanguage).delete();
    }

    public void updateLanguage() {
        updateKey(keyUpdateLanguage);
    }

    public boolean hasUpdatedLanguage() {
        return keyExist(keyUpdateLanguage);
    }

    public void updateVisibility() {
        updateKey(keyUpdateVisibility);
    }

    public boolean hasUpdatedVisibility() {
        return keyExist(keyUpdateVisibility);
    }

    private void updateKey(final String key) {
        final RSet<Integer> updateLanguage = redissonClient.getSet(key);
        updateLanguage.add(0);
        updateLanguage.expire(Duration.ofSeconds(10));
    }

    private boolean keyExist(final String key) {
        return redissonClient.getSet(key).isExists();
    }

}