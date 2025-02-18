package net.quillcraft.commons.party;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.lyflow.sqlrequest.SQLRequest;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.QuillCraftBungee;
import net.quillcraft.bungee.data.sql.table.SQLTablesManager;
import net.quillcraft.bungee.serialization.ProfileSerializationUtils;
import net.quillcraft.bungee.utils.StringUtils;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class Party {

    private UUID partyUUID;
    private UUID ownerUUID;
    private List<UUID> followersUUID;
    private List<String> followersName;
    private String ownerName;

    @JsonIgnore
    private SQLRequest sqlRequest;

    // For Redis
    private Party() {}

    public Party(ProxiedPlayer player) {
        this(player.getUniqueId(), player.getName());
    }

    public Party(UUID ownerUUID, String ownerNames) {
        this(UUID.randomUUID(), ownerUUID, ownerNames);
    }

    public Party(UUID partyUUID, UUID ownerUUID, String ownerName) {
        this(partyUUID, ownerUUID, ownerName, new ArrayList<>(), new ArrayList<>());
    }

    public Party(UUID partyUUID, UUID ownerUUID, String ownerNames, List<UUID> followersUUID, List<String> membersName) {
        this.partyUUID = partyUUID;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerNames;
        this.followersUUID = followersUUID;
        this.followersName = membersName;

        setSQLRequest();
    }

    public UUID getPartyUUID() {
        return partyUUID;
    }

    public ProxiedPlayer getOwner() {
        return ProxyServer.getInstance().getPlayer(ownerUUID);
    }

    public void setOwner(String name) {
        setOwner(name, getUUIDByFollowerName(name));
    }

    public void setOwner(UUID uuid) {
        setOwner(getNameByFollowerUUID(uuid), uuid);
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public List<String> getFollowersName() {
        return followersName;
    }

    public List<UUID> getFollowersUUID() {
        return followersUUID;
    }

    public List<UUID> getPlayersUUID() {
        final List<UUID> playersUUID = new ArrayList<>(followersUUID);
        playersUUID.add(ownerUUID);
        return playersUUID;
    }

    public List<ProxiedPlayer> getFollowers() {
        final List<ProxiedPlayer> followersList = new ArrayList<>();
        final ProxyServer proxyServer = ProxyServer.getInstance();

        followersUUID.stream().parallel().forEach(playerUUID -> followersList.add(proxyServer.getPlayer(playerUUID)));
        return followersList;
    }

    public List<ProxiedPlayer> getPlayers() {
        final List<ProxiedPlayer> playersList = getFollowers();
        playersList.add(getOwner());
        return playersList;
    }

    public void addPlayer(ProxiedPlayer player) {
        followersUUID.add(player.getUniqueId());
        followersName.add(player.getName());
        updateMembersData();
    }

    public void removePlayer(UUID uuid) {
        followersUUID.remove(uuid);
        followersName.remove(getNameByFollowerUUID(uuid));
        updateMembersData();
    }

    public void removePlayer(String name) {
        followersUUID.remove(getUUIDByFollowerName(name));
        followersName.remove(name);
        updateMembersData();
    }

    public void removePlayer(ProxiedPlayer player) {
        followersUUID.remove(player.getUniqueId());
        followersName.remove(player.getName());
        updateMembersData();
    }

    public void setOwner(String name, UUID uuid) {
        try {
            if(!followersUUID.contains(uuid)) throw new IllegalArgumentException("setOwner uuid isn't contains");
            if(!followersName.contains(name)) throw new IllegalArgumentException("setOwner name isn't contains");
        } catch(Exception exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            return;
        }

        ownerUUID = uuid;
        ownerName = name;

        followersUUID.remove(uuid);
        followersName.remove(name);

        getSQLRequest().addData("owner_uuid", ownerUUID);
        getSQLRequest().addData("owner_name", ownerName);
        updateMembersData();
    }

    protected void setSQLRequest() {
        final SQLTablesManager sqlTablesManager = SQLTablesManager.PARTY;
        this.sqlRequest = new SQLRequest(sqlTablesManager.getTable(), sqlTablesManager.getKeyColumn(), partyUUID.toString());
    }

    @Nullable
    public UUID getUUIDByFollowerName(String name) {
        final int index = StringUtils.indexOfStringIngoreCase(name, followersName);
        return index == -1 ? null : followersUUID.get(index);
    }

    @Nullable
    public String getNameByFollowerUUID(UUID uuid) {
        final int index = followersUUID.indexOf(uuid);
        return index == -1 ? null : followersName.get(index);
    }

    protected SQLRequest getSQLRequest() {
        return sqlRequest;
    }

    public List<ProxiedPlayer> getOnlinePlayers() {
        return getPlayers().stream().parallel().filter(Objects::nonNull).toList();
    }

    public List<UUID> getOnlineFollowersUUID() {
        final ProxyServer proxyServer = ProxyServer.getInstance();
        return getFollowersUUID().stream().parallel().filter(uuid -> proxyServer.getPlayer(uuid) != null).toList();
    }

    public List<UUID> getOfflineFollowersUUID() {
        final ProxyServer proxyServer = ProxyServer.getInstance();
        return getFollowersUUID().stream().parallel().filter(uuid -> proxyServer.getPlayer(uuid) == null).toList();
    }

    public List<String> getOfflineFollowersName() {
        final List<String> followersNameList = new ArrayList<>();
        getOfflineFollowersUUID().stream().parallel().forEach(uuid -> followersNameList.add(getNameByFollowerUUID(uuid)));

        return followersNameList;
    }

    private void updateMembersData() {
        getSQLRequest().addData("followers_uuid", new ProfileSerializationUtils.ListUUID().serialize(followersUUID));
        getSQLRequest().addData("followers_name", new ProfileSerializationUtils.ListString().serialize(followersName));
    }
}
