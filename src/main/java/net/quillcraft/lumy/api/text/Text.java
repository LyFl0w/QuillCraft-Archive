package net.quillcraft.lumy.api.text;


public enum Text implements TextBase{

    // COMMAND PART
    COMMAND_SETLANGUAGE_SELECTED("command.setlanguage.selected"),
    COMMAND_SETLANGUAGE_AUTO("command.setlanguage.auto"),
    COMMAND_SETLANGUAGE_DOESNT_EXIST("command.setlanguage.doesnt_exist"),
    COMMAND_SETLANGUAGE_SAME("command.setlanguage.same"),

    COMMAND_LOBBY_SUCCESS("command.lobby.success"),
    COMMAND_SETLOBBY_SUCCESS("command.setlobby.success"),
    COMMAND_SETLOBBY_ERROR_WORLD("command.setlobby.error_world"),


    // INVENTORY NAME PART
    INVENTORY_NAME_MENU("inventory_name.menu"),
    INVENTORY_NAME_VISIBILITY("inventory_name.visibility"),


    // ITEMS INVENTORY PART
    ITEMS_INVENTORY_LOBBY_MENU_NAME("items_inventory.lobby.menu.name"),
    ITEMS_INVENTORY_LOBBY_SHOP_NAME("items_inventory.lobby.shop.name"),
    ITEMS_INVENTORY_LOBBY_ACCOUNT_NAME("items_inventory.lobby.account.name"),
    ITEMS_INVENTORY_LOBBY_FRIENDS_NAME("items_inventory.lobby.friends.name"),
    ITEMS_INVENTORY_LOBBY_PARTICLES_NAME("items_inventory.lobby.particles.name"),
    ITEMS_INVENTORY_LOBBY_VISIBILITY_NAME("items_inventory.lobby.visibility.name"),
    ITEMS_INVENTORY_LOBBY_VISIBILITY_SAME("items_inventory.lobby.visibility.same"),
    ITEMS_INVENTORY_LOBBY_PARAMETERS_NAME("items_inventory.lobby.parameters.name"),
    ITEMS_INVENTORY_LOBBY_PARKOURPVP_NAME("items_inventory.lobby.parkourpvp.name"),


    // LOBBY PART
    LOBBY_PLAYER_JOIN("lobby.player.join"),
    LOBBY_PLAYER_LEAVE("lobby.player.leave"),


    // PARTY PART
    PARTY_GENERAL_JOIN("party.general_join"),
    PARTY_JOIN_SERVER("party.join_server"),
    PARTY_LEFT_SERVER("party.left_server"),
    PARTY_PERSO_JOIN("party.perso_join"),
    PARTY_PROMOTE_OWNER("party.promote_owner"),
    PARTY_UNPROMOTE_OWNER("party.unpromote_owner"),
    PARTY_OWN_YOUR_SELF("party.own_your_self"),
    PARTY_PERSO_LEAVE("party.perso_leave"),
    PARTY_GENERAL_LEAVE("party.general_leave"),
    PARTY_GENERAL_KICK("party.general_kick"),
    PARTY_PERSO_KICK("party.perso_kick"),
    PARTY_PLAYER_NOT_IN_YOUR_PARTY("party.player_not_in_your_party"),
    PARTY_PLAYER_IS_OFFLINE("party.player_is_offline"),
    PARTY_PLAYER_IS_ALREADY_IN_PARTY("party.player_is_already_in_party"),
    PARTY_PLAYER_IS_ALREADY_IN_YOUR_PARTY("party.player_is_already_in_your_party"),
    PARTY_PLAYER_IS_ALREADY_IN_ANOTHER_PARTY("party.player_is_already_in_another_party"),
    PARTY_MEMBERS_LIST("party.members_list"),
    PARTY_DELETE("party.delete"),
    PARTY_KICK_YOUR_SELF("party.kick_your_self"),
    PARTY_INVITE_YOUR_SELF("party.invite_your_self"),
    PARTY_ACCEPT_YOUR_SELF("party.accept_your_self"),
    PARTY_INVITATION_SEND("party.invitation_send"),
    PARTY_INVITATION_RECEIVED("party.invitation_received"),
    PARTY_HOVER_INVITATION_RECEIVED("party.hover_invitation_received"),
    PARTY_INVITATION_EXPIRED("party.invitation_expired"),
    PARTY_NOT_OWNER("party.not_owner"),
    PARTY_NO_PARTY("party.no_party"),


    // FRIEND PART
    FRIEND_INVITE_YOUR_SELF("friend.invite_your_self"),
    FRIEND_REMOVE_YOUR_SELF("friend.remove_your_self"),
    FRIEND_ACCEPT_YOUR_SELF("friend.accept_your_self"),
    FRIEND_DENY_YOUR_SELF("friend.deny_your_self"),
    FRIEND_JOIN_SERVER("friend.join_server"),
    FRIEND_LEFT_SERVER("friend.left_server"),
    FRIEND_NO_FRIENDS("friend.no_friends"),
    FRIEND_FRIENDS_LIST("friend.friends_list"),
    FRIEND_SEND_REQUEST("friend.send_request"),
    FRIEND_RECEIVED_REQUEST("friend.received_request"),
    FRIEND_NO_RECEIVED_REQUEST("friend.no_received_request"),
    FRIEND_PLAYER_IS_OFFLINE("friend.player_is_offline"),
    FRIEND_PLAYER_ALREADY_RECEIVED_REQUEST("friend.player_already_received_request"),
    FRIEND_PLAYER_IS_ALREADY_YOUR_FRIEND("friend.player_is_already_your_friend"),
    FRIEND_PLAYER_IS_NOT_YOUR_FRIEND("friend.player_is_not_your_friend"),
    FRIEND_PLAYER_DELETED_FROM_HIS_FRIEND("friend.player_deleted_from_his_friend"),
    FRIEND_PLAYER_ACCEPT_YOUR_REQUEST("friend.player_accept_your_request"),
    FRIEND_PLAYER_ACCEPT_REQUEST("friend.player_accept_request"),
    FRIEND_DELETED("friend.friend_deleted"),
    FRIEND_DECLINED_REQUEST("friend.declined_request"),
    FRIEND_HOVER_REQUEST("friend.hover_request"),


    // STATUS PART
    STATUS_PLAYER_OWNER("status.player.owner"),
    STATUS_PLAYER_ONLINE("status.player.online"),
    STATUS_PLAYER_OFFLINE("status.player.offline"),

    STATUS_VISIBILITY_EVERYONE("status.visibility.everyone"),
    STATUS_VISIBILITY_FRIENDS("status.visibility.friends"),
    STATUS_VISIBILITY_NOBODY("status.visibility.nobody"),


    // SERVER PART
    SERVER_VERSION_DOESNT_MATCHED("server.version_doesnt_matched"),


    // RANDOM PART
    SPAM("spam"),

    WORKING_PROGRESS("working_progress");

    private final String path;

    Text(String path){
        this.path = path;
    }

    @Override
    public String getPath(){
        return path;
    }

}
