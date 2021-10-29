package net.quillcraft.commons.friend;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import net.quillcraft.bungee.data.management.sql.DatabaseManager;
import net.quillcraft.bungee.serialization.ProfileSerializationUtils;
import net.quillcraft.commons.account.Account;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import reactor.util.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

public class FriendProvider {

    private final String keyFriends;
    private final RedissonClient redissonClient;
    private final UUID uuid;

    public FriendProvider(UUID uuid) {
        this.uuid = uuid;
        this.redissonClient = RedisManager.PLAYER_DATA.getRedisAccess().getRedissonClient();
        this.keyFriends = "friends:" + uuid.toString();
    }

    public FriendProvider(ProxiedPlayer player) {
        this(player.getUniqueId());
    }

    public FriendProvider(Account account) {
        this(account.getUUID());
    }

    public Friend getFriends() {
        Friend friends = getFriendsFromRedis();

        if (friends == null) {
            friends = getFriendsFromDatabase();
            sendFriendsToRedis(friends);
        }

        return friends;
    }

    private void sendFriendsToRedis(Friend friends) {
        redissonClient.getBucket(keyFriends).set(friends);
    }

    @Nullable
    private Friend getFriendsFromRedis() {
        final RBucket<Friend> friendRBucket = redissonClient.getBucket(keyFriends);
        return friendRBucket.get();
    }

    public Friend getFriendsFromDatabase() {

        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM friend WHERE uuid = ?");

            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                final List<UUID> friendsUUID = new ProfileSerializationUtils.ListUUID().
                        deserialize(resultSet.getString("friendsUUID"));
                final List<String> friendsName = new ProfileSerializationUtils.ListString().
                        deserialize(resultSet.getString("friendsName"));
                connection.close();

                return new Friend(friendsUUID, friendsName);
            }
            connection.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return new Friend();
    }

}