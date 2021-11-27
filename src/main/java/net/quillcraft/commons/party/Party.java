package net.quillcraft.commons.party;

import net.lyflow.sqlrequest.SQLRequest;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.data.management.sql.table.SQLTablesManager;
import net.quillcraft.bungee.serialization.ProfileSerializationUtils;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Party {

    private UUID partyUUID, ownerUUID;
    private List<UUID> followersUUID;
    private SQLRequest sqlRequest;
    private List<String> followersNames;
    private String ownerName;

    // For Redis
    public Party(){}

    public Party(ProxiedPlayer player){
        this(player.getUniqueId(), player.getName());
    }

    public Party(UUID ownerUUID, String ownerNames){
        this(UUID.randomUUID(), ownerUUID, ownerNames);
    }

    public Party(UUID partyUUID, UUID ownerUUID, String ownerNames){
        this(partyUUID, ownerUUID, ownerNames, new ArrayList<>(), new ArrayList<>());
    }

    public Party(UUID partyUUID, UUID ownerUUID, String ownerNames, List<UUID> followersUUID, List<String> membersNames){
        this.partyUUID = partyUUID;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerNames;
        this.followersUUID = followersUUID;
        this.followersNames = membersNames;

        final SQLTablesManager sqlTablesManager = SQLTablesManager.PARTY_DATA;
        this.sqlRequest = new SQLRequest(sqlTablesManager.getTable(), sqlTablesManager.getKeyColumn(), partyUUID.toString());
    }

    public UUID getPartyUUID(){
        return partyUUID;
    }

    public ProxiedPlayer getOwner(){
        return ProxyServer.getInstance().getPlayer(ownerUUID);
    }

    public UUID getOwnerUUID(){
        return ownerUUID;
    }

    public String getOwnerName(){
        return ownerName;
    }

    public List<String> getFollowersNames(){
        return followersNames;
    }

    public List<UUID> getFollowersUUID(){
        return followersUUID;
    }

    public List<UUID> getPlayersUUID(){
        final List<UUID> playersUUID = new ArrayList<>(followersUUID);
        playersUUID.add(ownerUUID);
        return playersUUID;
    }

    public List<ProxiedPlayer> getFollowers(){
        final List<ProxiedPlayer> followersList = new ArrayList<>();
        followersUUID.stream().parallel().
                forEach(playerUUID -> followersList.add(ProxyServer.getInstance().getPlayer(playerUUID)));
        return followersList;
    }

    public List<ProxiedPlayer> getPlayers(){
        final List<ProxiedPlayer> playersList = getFollowers();
        playersList.add(getOwner());
        return playersList;
    }

    public void addPlayer(ProxiedPlayer player){
        followersUUID.add(player.getUniqueId());
        followersNames.add(player.getName());
        getSQLRequest().addData("followersuuid", new ProfileSerializationUtils.ListUUID().serialize(followersUUID));
        getSQLRequest().addData("followersnames", new ProfileSerializationUtils.ListString().serialize(followersNames));
    }

    public void removePlayer(UUID uuid){
        followersUUID.remove(uuid);
        followersNames.remove(getNameByUUIDInFollowersList(uuid));
        getSQLRequest().addData("followersuuid", new ProfileSerializationUtils.ListUUID().serialize(followersUUID));
        getSQLRequest().addData("followersnames", new ProfileSerializationUtils.ListString().serialize(followersNames));
    }

    public void removePlayer(String name){
        followersUUID.remove(getUUIDByNameInFollowersList(name));
        followersNames.remove(name);
        getSQLRequest().addData("followersuuid", new ProfileSerializationUtils.ListUUID().serialize(followersUUID));
        getSQLRequest().addData("followersnames", new ProfileSerializationUtils.ListString().serialize(followersNames));
    }

    public void removePlayer(ProxiedPlayer player){
        followersUUID.remove(player.getUniqueId());
        followersNames.remove(player.getName());
        getSQLRequest().addData("followersuuid", new ProfileSerializationUtils.ListUUID().serialize(followersUUID));
        getSQLRequest().addData("followersnames", new ProfileSerializationUtils.ListString().serialize(followersNames));
    }

    public void setOwner(String name, UUID uuid){
        try{
            if(!followersUUID.contains(uuid)) throw new Exception("setOwner uuid isn't contains");
            if(!followersNames.contains(name)) throw new Exception("setOwner name isn't contains");
        }catch(Exception e){
            e.printStackTrace();
            return;
        }

        ownerUUID = uuid;
        ownerName = name;

        followersUUID.remove(uuid);
        followersNames.remove(name);

        getSQLRequest().addData("owneruuid", ownerUUID);
        getSQLRequest().addData("ownername", ownerName);
        getSQLRequest().addData("followersuuid", new ProfileSerializationUtils.ListUUID().serialize(followersUUID));
        getSQLRequest().addData("followersnames", new ProfileSerializationUtils.ListString().serialize(followersNames));
    }

    public void setOwner(String name){
        setOwner(name, getUUIDByNameInFollowersList(name));
    }

    public void setOwner(UUID uuid){
        setOwner(getNameByUUIDInFollowersList(uuid), uuid);
    }

    @Nullable
    public UUID getUUIDByNameInFollowersList(String name){
        for(int i = 0; i < followersNames.size(); i++)
            if(name.equalsIgnoreCase(followersNames.get(i))) return followersUUID.get(i);

        return null;
    }

    @Nullable
    public String getNameByUUIDInFollowersList(UUID uuid){
        for(int i = 0; i < followersUUID.size(); i++)
            if(uuid.equals(followersUUID.get(i))) return followersNames.get(i);

        return null;
    }

    protected SQLRequest getSQLRequest(){
        return sqlRequest;
    }

    public List<ProxiedPlayer> getOnlinePlayers(){
        return getPlayers().stream().parallel().filter(Objects::nonNull).toList();
    }

    public List<UUID> getOnlineFollowersUUID(){
        final ProxyServer proxyServer = ProxyServer.getInstance();
        return getFollowersUUID().stream().parallel().filter(uuid -> proxyServer.getPlayer(uuid)!=null)
                .toList();
    }

    public List<UUID> getOfflineFollowersUUID(){
        final ProxyServer proxyServer = ProxyServer.getInstance();
        return getFollowersUUID().stream().parallel().filter(uuid -> proxyServer.getPlayer(uuid) == null).toList();
    }

    public List<String> getOfflineFollowersName(){
        final List<String> followersNameList = new ArrayList<>();
        getOfflineFollowersUUID().stream().parallel().
                forEach(uuid -> followersNameList.add(getNameByUUIDInFollowersList(uuid)));

        return followersNameList;
    }
}
