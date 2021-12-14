package net.quillcraft.core.text;

public enum Text {

    COMMAND_SETLANGUAGE_SELECTED("core.command.setlanguage.selected"),
    COMMAND_SETLANGUAGE_AUTO("core.command.setlanguage.auto"),
    COMMAND_SETLANGUAGE_DOESNT_EXIST("core.command.setlanguage.doesnt_exist"),
    COMMAND_SETLANGUAGE_SAME("core.command.setlanguage.same"),

    PARTY_ACCEPT_YOUR_SELF("party.accept_your_self"),
    PARTY_KICK_YOUR_SELF("party.kick_your_self"),
    PARTY_INVITE_YOUR_SELF("party.invite_your_self"),
    PARTY_NO_PARTY("party.no_party"),

    FRIEND_INVITE_YOUR_SELF("friend.invite_your_self"),
    FRIEND_REMOVE_YOUR_SELF("friend.remove_your_self"),
    FRIEND_ACCEPT_YOUR_SELF("friend.accept_your_self"),
    FRIEND_DENY_YOUR_SELF("friend.deny_your_self"),

    SPAM("core.spam");

    private final String path;

    Text(final String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
