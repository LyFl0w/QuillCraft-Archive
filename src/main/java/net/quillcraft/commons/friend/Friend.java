package net.quillcraft.commons.friend;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Friend {

    private List<UUID> friendsUUID;
    private List<String> friendsName;

    // For Redis
    public Friend() {
        this.friendsUUID = new ArrayList<>();
        this.friendsName = new ArrayList<>();
    }

    public Friend(List<UUID> friendsUUID, List<String> friendsName) {
        this.friendsUUID = friendsUUID;
        this.friendsName = friendsName;
    }

    public List<String> getFriendsName() {
        return friendsName;
    }

    public List<UUID> getFriendsUUID() {
        return friendsUUID;
    }

    public List<ProxiedPlayer> getFriends(){
        final List<ProxiedPlayer> friendsList = new ArrayList<>();
        getFriendsUUID().stream().parallel().
                forEach(friendUUID -> friendsList.add(ProxyServer.getInstance().getPlayer(friendUUID)));
        return friendsList;
    }

    public List<ProxiedPlayer> getOnlineFriends(){
        return getFriends().stream().parallel().filter(Objects::nonNull).toList();
    }

    public List<UUID> getOnlineFriendsUUID(){
        return getFriendsUUID().stream().parallel().
                filter(uuid -> ProxyServer.getInstance().getPlayer(uuid) != null).toList();
    }

    public List<UUID> getOfflineFriendsUUID(){
        return getFriendsUUID().stream().parallel().
                filter(uuid -> ProxyServer.getInstance().getPlayer(uuid) == null).toList();
    }

}
