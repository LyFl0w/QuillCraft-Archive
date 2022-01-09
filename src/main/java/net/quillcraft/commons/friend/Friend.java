package net.quillcraft.commons.friend;

import net.lyflow.sqlrequest.SQLRequest;

import java.util.List;
import java.util.UUID;

public class Friend{

    private List<UUID> friendsUUID;
    private List<String> friendsName;
    private SQLRequest sqlRequest;

    // For Redis
    public Friend(){}

    public Friend(List<UUID> friendsUUID, List<String> friendsName){
        this.friendsUUID = friendsUUID;
        this.friendsName = friendsName;
    }
    public List<String> getFriendsName(){
        return friendsName;
    }

    public List<UUID> getFriendsUUID(){
        return friendsUUID;
    }

}
