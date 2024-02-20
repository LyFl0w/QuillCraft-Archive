package net.quillcraft.commons.friend;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.data.redis.RedisManager;
import net.quillcraft.bungee.data.sql.DatabaseManager;
import net.quillcraft.bungee.data.sql.table.SQLTablesManager;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.bungee.serialization.ProfileSerializationUtils;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.exception.FriendNotFoundException;
import net.quillcraft.lumy.api.text.Text;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import reactor.util.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class FriendProvider {

    private final String keyFriends;
    private final RedissonClient redissonClient;
    private final UUID uuid;
    private final ProxiedPlayer player;
    private final SQLTablesManager sqlTablesManager;

    public FriendProvider(ProxiedPlayer player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.redissonClient = RedisManager.FRIEND.getRedisAccess().getRedissonClient();
        this.keyFriends = "friends:"+uuid.toString();
        this.sqlTablesManager = SQLTablesManager.FRIEND;
    }

    public FriendProvider(UUID uuid) {
        this.player = null;
        this.uuid = uuid;
        this.redissonClient = RedisManager.FRIEND.getRedisAccess().getRedissonClient();
        this.keyFriends = "friends:"+uuid.toString();
        this.sqlTablesManager = SQLTablesManager.FRIEND;
    }

    public Friend getFriends() throws FriendNotFoundException {
        Friend friends = getFriendsFromRedis();

        if(friends == null) {
            friends = getFriendsFromDatabase();
            sendFriendsToRedis(friends);
        } else {
            friends.setSQLRequest(uuid);
            redissonClient.getBucket(keyFriends).clearExpire();
        }

        return friends;
    }

    private void sendFriendsToRedis(Friend friends) {
        redissonClient.getBucket(keyFriends).set(friends);
    }

    private void updateFriendDatabase(PreparedStatement updateRequest) {
        try {
            updateRequest.executeUpdate();
            updateRequest.getConnection().close();
        } catch(Exception exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    @Nullable
    private Friend getFriendsFromRedis() {
        final RBucket<Friend> friendRBucket = redissonClient.getBucket(keyFriends);
        return friendRBucket.get();
    }

    private Friend getFriendsFromDatabase() throws FriendNotFoundException {
        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + sqlTablesManager.getTable() + " WHERE " + sqlTablesManager.getKeyColumn() + " = ?")) {

            preparedStatement.setString(1, uuid.toString());
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final List<UUID> friendsUUID = new ProfileSerializationUtils.ListUUID().deserialize(resultSet.getString("friends_uuid"));
                    final List<String> friendsName = new ProfileSerializationUtils.ListString().deserialize(resultSet.getString("friends_name"));

                    return new Friend(uuid, friendsUUID, friendsName);
                }
            }
        } catch (Exception exception) {
            throw new FriendNotFoundException(uuid);
        }

        return createFriendInDatabase();
    }

    public void updateFriends(Friend friend) {
        try (final PreparedStatement updateRequest = friend.getSQLRequest().getUpdateRequest(DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection())){
            sendFriendsToRedis(friend);
            updateFriendDatabase(updateRequest);
        } catch(Exception exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }


    private Friend createFriendInDatabase() {
        final Friend friend = new Friend(uuid);
        try (final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + sqlTablesManager.getTable() + " (uuid, friends_uuid, friends_name) VALUES (?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, new ProfileSerializationUtils.ListUUID().serialize(friend.getFriendsUUID()));
            preparedStatement.setString(3, new ProfileSerializationUtils.ListString().serialize(friend.getFriendsName()));

            preparedStatement.execute();
        } catch (SQLException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return friend;
    }

    public boolean hasRequested(ProxiedPlayer targetPlayer) {
        return redissonClient.getSet("friendAdd:"+uuid.toString()+":"+targetPlayer.getUniqueId().toString()).delete();
    }

    public boolean sendAddRequest(ProxiedPlayer targetPlayer, Account targetAccount) {
        final RSet<Integer> inviteBucket = redissonClient.getSet("friendAdd:"+uuid.toString()+":"+targetPlayer.getUniqueId().toString());
        if(inviteBucket.isExists()) return false;

        inviteBucket.add(0);
        inviteBucket.expire(Duration.ofMinutes(3));

        final String playerName = player.getName();
        final LanguageManager languageManager = LanguageManager.getLanguage(targetAccount);
        final TextComponent textComponent = languageManager.getMessageComponentReplace(Text.FRIEND_RECEIVED_REQUEST, "%PLAYER%", playerName);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(new ComponentBuilder(languageManager.getMessage(Text.FRIEND_HOVER_REQUEST)).create())));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept "+playerName));

        targetPlayer.sendMessage(textComponent);
        return true;
    }

    public void expireRedis() {
        redissonClient.getBucket(keyFriends).expire(Duration.ofHours(6));
    }
}