package net.quillcraft.commons.party;

import net.lyflow.sqlrequest.SQLRequest;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.quillcraft.bungee.data.management.sql.table.SQLTablesManager;
import net.quillcraft.bungee.serialization.ProfileSerializationUtils;
import net.quillcraft.bungee.utils.StringUtils;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Party {

    private UUID partyUUID, ownerUUID;
    private List<UUID> followersUUID;
    private SQLRequest sqlRequest;
    private List<String> followersName;
    private String ownerName;

    // For Redis
    public Party(){}

    public Party(ProxiedPlayer player){
        this(player.getUniqueId(), player.getName());
    }

    public Party(UUID ownerUUID, String ownerNames){
        this(UUID.randomUUID(), ownerUUID, ownerNames);
    }

    public Party(UUID partyUUID, UUID ownerUUID, String ownerName){
        this(partyUUID, ownerUUID, ownerName, new ArrayList<>(), new ArrayList<>());
    }

    public Party(UUID partyUUID, UUID ownerUUID, String ownerNames, List<UUID> followersUUID, List<String> membersName){
        this.partyUUID = partyUUID;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerNames;
        this.followersUUID = followersUUID;
        this.followersName = membersName;

        final SQLTablesManager sqlTablesManager = SQLTablesManager.PARTY;
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

    public List<String> getFollowersName(){
        return followersName;
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
        followersName.add(player.getName());
        updateMembersData();
    }

    public void removePlayer(UUID uuid){
        followersUUID.remove(uuid);
        followersName.remove(getNameByFollowerUUID(uuid));
        updateMembersData();
    }

    public void removePlayer(String name){
        followersUUID.remove(getUUIDByFollowerName(name));
        followersName.remove(name);
        updateMembersData();
    }

    public void removePlayer(ProxiedPlayer player){
        followersUUID.remove(player.getUniqueId());
        followersName.remove(player.getName());
        updateMembersData();
    }

    public void setOwner(String name, UUID uuid){
        try{
            if(!followersUUID.contains(uuid)) throw new Exception("setOwner uuid isn't contains");
            if(!followersName.contains(name)) throw new Exception("setOwner name isn't contains");
        }catch(Exception e){
            e.printStackTrace();
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

    public void setOwner(String name){
        setOwner(name, getUUIDByFollowerName(name));
    }

    public void setOwner(UUID uuid){
        setOwner(getNameByFollowerUUID(uuid), uuid);
    }

    @Nullable
    public UUID getUUIDByFollowerName(String name){
        final int index = StringUtils.indexOfStringIngoreCase(name, followersName);
        return index == -1 ? null : followersUUID.get(index);

        /* OLD version
        for(int i = 0; i < followers_name.size(); i++)
            if(name.equalsIgnoreCase(followers_name.get(i))) return followersUUID.get(i);

        return null;*/
    }

    @Nullable
    public String getNameByFollowerUUID(UUID uuid){
        final int index = followersUUID.indexOf(uuid);
        return index == -1 ? null : followersName.get(index);
        /* OLD version
        for(int i = 0; i < followersUUID.size(); i++)
            if(uuid.equals(followersUUID.get(i))) return followers_name.get(i);

        return null;*/
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
                forEach(uuid -> followersNameList.add(getNameByFollowerUUID(uuid)));

        return followersNameList;
    }

    private void updateMembersData(){
        getSQLRequest().addData("followers_uuid", new ProfileSerializationUtils.ListUUID().serialize(followersUUID));
        getSQLRequest().addData("followers_name", new ProfileSerializationUtils.ListString().serialize(followersName));
    }
}
