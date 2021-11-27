package net.quillcraft.commons.friend;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.data.management.redis.RedisManager;
import net.quillcraft.bungee.data.management.sql.DatabaseManager;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.bungee.serialization.ProfileSerializationUtils;
import net.quillcraft.bungee.text.Text;
import net.quillcraft.commons.account.Account;

import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.FriendNotFoundException;
import net.quillcraft.commons.party.Party;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import reactor.util.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FriendProvider {

    private final String keyFriends;
    private final RedissonClient redissonClient;
    private final UUID uuid;
    private final ProxiedPlayer player;

    public FriendProvider(ProxiedPlayer player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.redissonClient = RedisManager.FRIEND_DATA.getRedisAccess().getRedissonClient();
        this.keyFriends = "friends:" + uuid.toString();
    }

    public FriendProvider(UUID uuid) {
        this(QuillCraftBungee.getInstance().getProxy().getPlayer(uuid));
    }

    public FriendProvider(Account account) {
        this(account.getUUID());
    }

    public Friend getFriends() throws FriendNotFoundException{
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

    private void updatePartyDatabase(PreparedStatement updateRequest){
        try{
            updateRequest.executeUpdate();
            updateRequest.getConnection().close();
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    @Nullable
    private Friend getFriendsFromRedis() {
        final RBucket<Friend> friendRBucket = redissonClient.getBucket(keyFriends);
        return friendRBucket.get();
    }

    private Friend getFriendsFromDatabase() throws FriendNotFoundException{
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

                return new Friend(uuid, friendsUUID, friendsName);
            }else{
                connection.close();
                return createFriendInDatabase();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        throw new FriendNotFoundException(uuid);
    }

    public void updateFriends(Friend friend){
        PreparedStatement updateRequest = null;
        try{
            updateRequest = friend.getSQLRequest().getUpdateRequest(DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection());
        }catch(Exception exception){
            exception.printStackTrace();
        }
        sendFriendsToRedis(friend);
        updatePartyDatabase(updateRequest);
    }


    private Friend createFriendInDatabase(){
        final Friend friend = new Friend(uuid);
        try{
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO frienddata (uuid, friendsUUID, friendsName) VALUES (?,?,?)");

            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, new ProfileSerializationUtils.ListUUID().serialize(friend.getFriendsUUID()));
            preparedStatement.setString(3, new ProfileSerializationUtils.ListString().serialize(friend.getFriendsName()));

            preparedStatement.execute();

            connection.close();
        }catch(SQLException exception){
            exception.printStackTrace();
        }
        return friend;
    }

    public boolean hasRequested(ProxiedPlayer targetPlayer){
        return redissonClient.getSet("friendAdd:"+uuid.toString()+":"+targetPlayer.getUniqueId().toString()).delete();
    }

    public boolean sendAddRequest(ProxiedPlayer targetPlayer, Account targetAccount){
        final RSet<Integer> inviteBucket = redissonClient.getSet("friendAddd:"+uuid.toString()+":"+targetPlayer.getUniqueId().toString());
        if(inviteBucket.isExists()) return false;

        inviteBucket.add(0);
        inviteBucket.expire(3, TimeUnit.MINUTES);

        final LanguageManager languageManager = LanguageManager.getLanguage(targetAccount);
        final TextComponent textComponent = new TextComponent("Vous avez recu une demande d'ami de %PLAYER%".replace("%PLAYER%", player.getName()));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new net.md_5.bungee.api.chat.hover.content.Text(new ComponentBuilder("Cliquer pour accepter la demande d'ami").create())));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept "+player.getName()));

        targetPlayer.sendMessage(textComponent);
        return true;
    }

}