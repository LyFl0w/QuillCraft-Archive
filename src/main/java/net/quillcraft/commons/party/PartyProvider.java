package net.quillcraft.commons.party;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.exception.PartyNotFoundException;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.redis.RedisManager;
import net.quillcraft.core.data.sql.DatabaseManager;
import net.quillcraft.core.data.sql.table.SQLTablesManager;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.serialization.ProfileSerializationUtils;
import net.quillcraft.lumy.api.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PartyProvider {

    private final RedissonClient redissonClient;
    private final SQLTablesManager sqlTablesManager;

    private final Player player;
    private UUID partyUUID;
    private String keyParty;

    public PartyProvider(Account account) {
        this.player = Bukkit.getPlayer(account.getUUID());
        this.partyUUID = account.getPartyUUID();
        this.redissonClient = RedisManager.PARTY.getRedisAccess().getRedissonClient();
        this.sqlTablesManager = SQLTablesManager.PARTY;

        updatePartyKeys(account);
    }

    @Nullable
    public Party getParty() throws PartyNotFoundException {
        if (keyParty == null) {
            player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.PARTY_NO_PARTY));
            return null;
        }

        Party party = getPartyFromRedis();

        if (party == null) {
            party = getPartyFromDatabase();
            sendPartyToRedis(party);
        } else {
            redissonClient.getBucket(keyParty).clearExpire();
        }

        return party;
    }

    private Party getPartyFromRedis() {
        final RBucket<Party> accountRBucket = redissonClient.getBucket(keyParty);

        return accountRBucket.get();
    }

    private Party getPartyFromDatabase() throws PartyNotFoundException {
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final ResultSet resultSet;
            try (final PreparedStatement preparedStatement =
                         connection.prepareStatement("SELECT * FROM " + sqlTablesManager.getTable() + " WHERE " + sqlTablesManager.getKeyColumn() + " = ?")) {

                preparedStatement.setString(1, partyUUID.toString());
                preparedStatement.executeQuery();

                resultSet = preparedStatement.getResultSet();
            }

            if (resultSet.next()) {
                final UUID ownerUUID = UUID.fromString(resultSet.getString("owner_uuid"));
                final String ownerName = resultSet.getString("owner_name");
                final List<UUID> followersUUID = new ProfileSerializationUtils.ListUUID().deserialize(resultSet.getString("followers_uuid"));
                final List<String> followersName = new ProfileSerializationUtils.ListString().deserialize(resultSet.getString("followers_name"));

                connection.close();

                return new Party(partyUUID, ownerUUID, ownerName, followersUUID, followersName);
            }
            connection.close();
        } catch (SQLException exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }

        throw new PartyNotFoundException(player.getUniqueId());
    }

    private void sendPartyToRedis(Party party) {
        final RBucket<Party> partyRBucket = redissonClient.getBucket(keyParty);
        partyRBucket.set(party);
    }

    private void updatePartyKeys(Account account) {
        if (account.hasParty()) {
            this.partyUUID = account.getPartyUUID();
            this.keyParty = "party:" + partyUUID.toString();
        }
    }
}
