package net.quillcraft.commons.friend;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

public class Friend{

    private List<UUID> friendsUUID;
    private List<String> friendsName;

    @JsonIgnore
    private SQLRequest sqlRequest;

    // For Redis
    private Friend(){}

    public Friend(UUID uuid){
        this(uuid, new ArrayList<>(), new ArrayList<>());
    }

    public Friend(UUID uuid, List<UUID> friendsUUID, List<String> friendsName){
        this.friendsUUID = friendsUUID;
        this.friendsName = friendsName;

        setSQLRequest(uuid);
    }

    public List<String> getFriendsName(){
        return friendsName;
    }

    public List<UUID> getFriendsUUID(){
        return friendsUUID;
    }

    public List<ProxiedPlayer> getFriends(){
        final List<ProxiedPlayer> friendsList = new ArrayList<>();
        getFriendsUUID().stream().parallel().forEach(friendUUID -> friendsList.add(ProxyServer.getInstance().getPlayer(friendUUID)));
        return friendsList;
    }

    @Nullable
    public String getNameByUUIDInFollowersList(UUID uuid){
        for(int i = 0; i < friendsUUID.size(); i++)
            if(uuid.equals(friendsUUID.get(i))) return friendsName.get(i);

        return null;
    }

    public List<ProxiedPlayer> getOnlineFriends(){
        return getFriends().stream().parallel().filter(Objects::nonNull).toList();
    }

    public List<UUID> getOnlineFriendsUUID(){
        return getFriendsUUID().stream().parallel().filter(uuid -> ProxyServer.getInstance().getPlayer(uuid) != null).toList();
    }

    public List<UUID> getOfflineFriendsUUID(){
        return getFriendsUUID().stream().parallel().filter(uuid -> ProxyServer.getInstance().getPlayer(uuid) == null).toList();
    }

    public List<String> getOfflineFriendsName(){
        final List<String> friendsNameList = new ArrayList<>();
        getOfflineFriendsUUID().forEach(uuid -> friendsNameList.add(getNameByUUIDInFollowersList(uuid)));

        return friendsNameList;
    }

    protected SQLRequest getSQLRequest(){
        return sqlRequest;
    }

    protected void setSQLRequest(UUID uuid){
        final SQLTablesManager sqlTablesManager = SQLTablesManager.FRIEND;
        this.sqlRequest = new SQLRequest(sqlTablesManager.getTable(), sqlTablesManager.getKeyColumn(), uuid.toString());
    }

    public void addPlayer(ProxiedPlayer targetPlayer){
        friendsName.add(targetPlayer.getName());
        friendsUUID.add(targetPlayer.getUniqueId());
        updateFriendData();
    }

    public void removePlayer(UUID uuid){
        final int index = friendsUUID.indexOf(uuid);
        friendsName.remove(index);
        friendsUUID.remove(index);
        updateFriendData();
    }

    @Nullable
    public UUID getUUIDByFriendName(String name){
        final int index = StringUtils.indexOfStringIngoreCase(name, friendsName);
        return index == -1 ? null : friendsUUID.get(index);
    }

    @Nullable
    public String getNameByFriendUUID(UUID uuid){
        final int index = friendsUUID.indexOf(uuid);
        return index == -1 ? null : friendsName.get(index);
    }

    private void updateFriendData(){
        getSQLRequest().addData("friends_uuid", new ProfileSerializationUtils.ListUUID().serialize(friendsUUID));
        getSQLRequest().addData("friends_name", new ProfileSerializationUtils.ListString().serialize(friendsName));
    }
}
