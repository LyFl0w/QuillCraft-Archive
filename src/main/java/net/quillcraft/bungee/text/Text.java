package net.quillcraft.bungee.text;

public enum Text {

    PARTY_PERSO_JOIN("party.perso_join"),
    PARTY_GENERAL_JOIN("party.general_join"),
    PARTY_PROMOTE_OWNER("party.promote_owner"),
    PARTY_UNPROMOTE_OWNER("party.unpromote_owner"),
    PARTY_OWN_YOUR_SELF("party.own_your_self"),
    PARTY_PERSO_LEAVE("party.perso_leave"),
    PARTY_GENERAL_LEAVE("party.general_leave"),
    PARTY_GENERAL_KICK("party.general_kick"),
    PARTY_PERSO_KICK("party.perso_kick"),
    PARTY_PLAYER_NOT_IN_YOUR_PARTY("party.player_not_in_your_party"),
    PARTY_PLAYER_IS_OFFLINE("party.player_is_offline"),
    PARTY_PLAYER_IS_ALREADY_IN_YOUR_PARTY("party.player_is_already_in_your_party"),
    PARTY_PLAYER_IS_ALREADY_IN_ANOTHER_PARTY("party.player_is_already_in_another_party"),
    PARTY_PLAYER_ARE_ALREADY_IN_PARTY("party.player_are_already_in_party"),
    PARTY_NOT_OWNER("party.not_owner"),
    PARTY_DELETE("party.delete"),
    PARTY_PLAYERS_DEFAULT_MESSAGE("party.players.default_message"),
    PARTY_PLAYERS_OWNER("party.players.owner"),
    PARTY_PLAYERS_ONLINE("party.players.online"),
    PARTY_PLAYERS_OFFLINE("party.players.offline"),
    PARTY_INVITATION_SEND("party.invitation_send"),
    PARTY_INVITATION_RECEIVED("party.invitation_received"),
    PARTY_HOVER_INVITATION_RECEIVED("party.hover_invitation_received"),
    PARTY_INVITATION_EXPIRED("party.invitation_expired"),
    PARTY_NO_PARTY("party.no_party"),
    PARTY_JOIN_SERVER("party.join_server"),
    PARTY_LEFT_SERVER("party.left_server"),
    PARTY_PLAYERS_MEMBERS_MESSAGE("party.players.members_message"),

    SERVER_VERSION_DOESNT_MATCHED("server.version_doesnt_matched");

    private final String path;

    Text(final String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }

}
