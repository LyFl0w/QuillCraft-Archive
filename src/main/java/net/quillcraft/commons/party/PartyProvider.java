package net.quillcraft.commons.party;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.data.redis.RedisManager;
import net.quillcraft.bungee.data.sql.DatabaseAccess;
import net.quillcraft.bungee.data.sql.DatabaseManager;
import net.quillcraft.bungee.data.sql.table.SQLTablesManager;
import net.quillcraft.bungee.manager.LanguageManager;
import net.quillcraft.bungee.serialization.ProfileSerializationUtils;
import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.commons.exception.PartyNotFoundException;
import org.jetbrains.annotations.Nullable;
import org.lumy.api.text.Text;
import org.lumy.api.text.TextList;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PartyProvider {

    private final RedissonClient redissonClient;
    private final SQLTablesManager sqlTablesManager;

    private final ProxiedPlayer player;
    private UUID partyUUID;
    private String keyParty;

    public PartyProvider(Account account) {
        this.player = ProxyServer.getInstance().getPlayer(account.getUUID());
        this.partyUUID = account.getPartyUUID();
        this.redissonClient = RedisManager.PARTY.getRedisAccess().getRedissonClient();
        this.sqlTablesManager = SQLTablesManager.PARTY;

        updatePartyKeys(account);
    }

    @Nullable
    public Party getParty() {
        if(keyParty == null) {
            player.sendMessage(LanguageManager.getLanguage(player).getMessageComponent(Text.PARTY_NO_PARTY));
            return null;
        }

        Party party = getPartyFromRedis();

        if(party == null) {
            party = getPartyFromDatabase();
            sendPartyToRedis(party);
        } else {
            party.setSQLRequest();
            redissonClient.getBucket(keyParty).clearExpire();
        }

        return party;
    }

    public Party createParty() {
        Party party = new Party(player);
        updatePartyKeys(party);

        sendPartyToRedis(party);
        createPartyInDatabase(party);
        return party;
    }

    public void deletePartyFromRedis() {
        redissonClient.getBucket(keyParty).delete();
    }

    public void deletePartyFromDatabase(Party party) {
        final DatabaseAccess databaseManager = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess();
        try {
            party.getSQLRequest().sendDeleteRequest(databaseManager.getConnection());

            for(UUID uuid : party.getPlayersUUID()) {
                final AccountProvider accountProvider = new AccountProvider(uuid);
                boolean hasAccountOnRedis = true;
                Account account = accountProvider.getAccountFromRedis();
                if(account == null) {
                    account = accountProvider.getAccountFromDatabase();
                    hasAccountOnRedis = false;
                }
                account.setSQLRequest();
                account.setPartyUUID(null);
                if(hasAccountOnRedis) {
                    accountProvider.updateAccount(account);
                } else {
                    accountProvider.updateAccountDatabase(account.getSQLRequest().getUpdateRequest(databaseManager.getConnection()));
                }
            }

        } catch(SQLException|AccountNotFoundException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    public void deleteParty(Party party) {
        //DELETE PARTY FROM REDIS
        deletePartyFromRedis();
        //DELETE PARTY FROM DB
        deletePartyFromDatabase(party);
    }

    public void sendMessageToPlayers(Party party, TextList textList, String oldChar, String newChar) {
        party.getOnlinePlayers().stream().parallel().forEach(player -> LanguageManager.getLanguage(player).getMessage(textList).forEach(message -> player.sendMessage(new TextComponent(message.replace(oldChar, newChar)))));
    }

    public void sendMessageToPlayers(Party party, TextList textList) {
        party.getOnlinePlayers().stream().parallel().forEach(player -> LanguageManager.getLanguage(player).getMessage(textList).forEach(message -> player.sendMessage(new TextComponent(message))));
    }

    public void sendMessageToPlayers(Party party, Text text, String oldChar, String newChar) {
        party.getOnlinePlayers().stream().parallel().forEach(player -> player.sendMessage(LanguageManager.getLanguage(player).getMessageComponentReplace(text, oldChar, newChar)));
    }

    public void sendMessageToPlayers(Party party, Text text) {
        party.getOnlinePlayers().stream().parallel().forEach(player -> player.sendMessage(LanguageManager.getLanguage(player).getMessageComponent(text)));
    }

    public void sendMessageToPlayers(Party party, String message) {
        party.getOnlinePlayers().stream().parallel().forEach(player -> player.sendMessage(new TextComponent(message)));
    }

    private Party getPartyFromRedis() {
        final RBucket<Party> accountRBucket = redissonClient.getBucket(keyParty);

        return accountRBucket.get();
    }

    private Party getPartyFromDatabase() {
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+sqlTablesManager.getTable()+" WHERE "+sqlTablesManager.getKeyColumn()+" = ?");

            preparedStatement.setString(1, partyUUID.toString());
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();
            if(resultSet.next()) {
                final UUID ownerUUID = UUID.fromString(resultSet.getString("owner_uuid"));
                final String ownerName = resultSet.getString("owner_name");
                final List<UUID> followersUUID = new ProfileSerializationUtils.ListUUID().deserialize(resultSet.getString("followers_uuid"));
                final List<String> followersName = new ProfileSerializationUtils.ListString().deserialize(resultSet.getString("followers_name"));

                connection.close();

                return new Party(partyUUID, ownerUUID, ownerName, followersUUID, followersName);
            }
            connection.close();

        } catch(SQLException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }

        throw new PartyNotFoundException(player);
    }

    public void updateParty(Party party) {
        PreparedStatement updateRequest = null;
        try {
            updateRequest = party.getSQLRequest().getUpdateRequest(DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection());
        } catch(Exception exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        sendPartyToRedis(party);
        updatePartyDatabase(updateRequest);
    }

    private void createPartyInDatabase(Party party) {
        try {
            final Connection connection = DatabaseManager.MINECRAFT_SERVER.getDatabaseAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+sqlTablesManager.getTable()+" (party_uuid, owner_uuid, owner_name, followers_uuid, followers_name) VALUES (?,?,?,?,?)");

            preparedStatement.setString(1, party.getPartyUUID().toString());
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.setString(3, player.getName());
            preparedStatement.setString(4, new ProfileSerializationUtils.ListUUID().serialize(party.getFollowersUUID()));
            preparedStatement.setString(5, new ProfileSerializationUtils.ListString().serialize(party.getFollowersName()));

            preparedStatement.execute();

            connection.close();
        } catch(SQLException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private void sendPartyToRedis(Party party) {
        final RBucket<Party> partyRBucket = redissonClient.getBucket(keyParty);
        partyRBucket.set(party);
    }

    private void updatePartyDatabase(PreparedStatement updateRequest) {
        try {
            updateRequest.executeUpdate();
            updateRequest.getConnection().close();
        } catch(Exception exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    private void updatePartyKeys(Party party) {
        this.partyUUID = party.getPartyUUID();
        this.keyParty = "party:"+partyUUID.toString();
    }

    private void updatePartyKeys(Account account) {
        if(account.hasParty()) {
            this.partyUUID = account.getPartyUUID();
            this.keyParty = "party:"+partyUUID.toString();
        }
    }

    public boolean hasInvited(ProxiedPlayer targetPlayer) {
        return redissonClient.getSet("partyAdd:"+partyUUID.toString()+":"+targetPlayer.getUniqueId().toString()).delete();
    }

    public boolean sendInviteRequest(ProxiedPlayer targetPlayer, Account targetAccount) {
        final RSet<Integer> inviteBucket = redissonClient.getSet("partyAdd:"+partyUUID.toString()+":"+targetPlayer.getUniqueId().toString());
        if(inviteBucket.isExists()) return false;

        inviteBucket.add(0);
        inviteBucket.expire(Duration.ofMinutes(3));

        final LanguageManager languageManager = LanguageManager.getLanguage(targetAccount);
        final TextComponent textComponent = languageManager.getMessageComponentReplace(Text.PARTY_INVITATION_RECEIVED, "%PLAYER%", player.getName());
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(new ComponentBuilder(languageManager.getMessage(Text.PARTY_HOVER_INVITATION_RECEIVED)).create())));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept "+player.getName()));

        targetPlayer.sendMessage(textComponent);
        return true;
    }

    public void expireRedis() {
        redissonClient.getBucket(keyParty).expire(Duration.ofHours(6));
    }

    public String getKeyParty() {
        return keyParty;
    }
}
