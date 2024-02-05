package net.quillcraft.commons.friend;

import net.quillcraft.commons.exception.FriendNotFoundException;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.data.redis.RedisManager;
import net.quillcraft.core.data.sql.DatabaseManager;
import net.quillcraft.core.data.sql.table.SQLTablesManager;
import net.quillcraft.core.serialization.ProfileSerializationUtils;
import org.bukkit.entity.Player;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import reactor.util.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class FriendProvider {

    private final String keyFriends;
    private final RedissonClient redissonClient;
    private final UUID uuid;
    private final SQLTablesManager sqlTablesManager;

    public FriendProvider(Player player) {
        this.uuid = player.getUniqueId();
        this.redissonClient = RedisManager.FRIEND.getRedisAccess().getRedissonClient();
        this.keyFriends = "friends:" + uuid;
        this.sqlTablesManager = SQLTablesManager.FRIEND;
    }

    public Friend getFriends() throws FriendNotFoundException {
        Friend friends = getFriendsFromRedis();

        if (friends == null) {
            friends = getFriendsFromDatabase();
            sendFriendsToRedis(friends);
        }

        return friends;
    }

    @Nullable
    private Friend getFriendsFromRedis() {
        final RBucket<Friend> friendRBucket = redissonClient.getBucket(keyFriends);
        return friendRBucket.get();
    }

    private Friend getFriendsFromDatabase() throws FriendNotFoundException {
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            try (final PreparedStatement preparedStatement
                         = connection.prepareStatement("SELECT * FROM " + sqlTablesManager.getTable() + " WHERE " + sqlTablesManager.getKeyColumn() + " = ?")) {

                preparedStatement.setString(1, uuid.toString());
                preparedStatement.executeQuery();

                final ResultSet resultSet = preparedStatement.getResultSet();

                if (resultSet.next()) {
                    final List<UUID> friendsUUID = new ProfileSerializationUtils.ListUUID().deserialize(resultSet.getString("friends_uuid"));
                    final List<String> friendsName = new ProfileSerializationUtils.ListString().deserialize(resultSet.getString("friends_name"));
                    connection.close();

                    return new Friend(friendsUUID, friendsName);
                } else {
                    connection.close();
                    return createFriendInDatabase();
                }
            }

        } catch (Exception exception) {
            throw new FriendNotFoundException(uuid);
        }
    }

    private void sendFriendsToRedis(Friend friends) {
        redissonClient.getBucket(keyFriends).set(friends);
    }

    private Friend createFriendInDatabase() {
        final Friend friend = new Friend(new ArrayList<>(), new ArrayList<>());
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            try (final PreparedStatement preparedStatement =
                         connection.prepareStatement("INSERT INTO " + sqlTablesManager.getTable() + " (uuid, friends_uuid, friends_name) VALUES (?, ?, ?)")) {

                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, new ProfileSerializationUtils.ListUUID().serialize(friend.getFriendsUUID()));
                preparedStatement.setString(3, new ProfileSerializationUtils.ListString().serialize(friend.getFriendsName()));

                preparedStatement.execute();

                connection.close();
            }

        } catch (SQLException exception) {
            QuillCraftCore.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return friend;
    }

}