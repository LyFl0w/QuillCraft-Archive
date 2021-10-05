package net.quillcraft.lobby.text;

public enum Text {

    WORKING_PROGRESS("lobby.working_progress"),

    MENU_INVENTORY("lobby.net.quillcraft.lobby.inventory.menu"),
    VISIBILITY_INVENTORY("lobby.net.quillcraft.lobby.inventory.visibility"),

    MENU_ITEM("lobby.item.menu.name"),
    SHOP_ITEM("lobby.item.shop.name"),
    ACCOUNT_ITEM("lobby.item.account.name"),
    FRIENDS_ITEM("lobby.item.friends.name"),
    PARTICLES_ITEM("lobby.item.particles.name"),
    VISIBILITY_ITEM("lobby.item.visibility.name"),
    PARAMETERS_ITEM("lobby.item.parameters.name"),
    INVENTORY_MENU_PARKOURPVP_ITEM("lobby.item.parkourpvp.name"),

    VISIBILITY_ITEM_STATUS_EVERYONE("lobby.item.visibility.status.everyone"),
    VISIBILITY_ITEM_STATUS_FRIENDS("lobby.item.visibility.status.friends"),
    VISIBILITY_ITEM_STATUS_NOBODY("lobby.item.visibility.status.nobody"),

    VISIBILITY_CHANGE_SAME("lobby.visibility_change.same"),

    PLAYER_JOIN("lobby.net.quillcraft.lobby.player.join"),
    PLAYER_LEAVE("lobby.net.quillcraft.lobby.player.leave"),

    COMMAND_SETLOBBY_SUCCESS("lobby.commands.setlobby.success"),
    COMMAND_SETLOBBY_ERROR_WORLD("lobby.commands.setlobby.error_world"),

    COMMAND_LOBBY_SUCCESS("lobby.commands.lobby.success"),

    TABLIST_LOBBY_HEADER("lobby.tablist.default.header"),
    TABLIST_LOBBY_FOOTER("lobby.tablist.default.footer"),

    TITLE_JOIN_LOBBY("lobby.join.title"),
    SUBTITLE_JOIN_LOBBY("lobby.join.subtitle"),

    LANGUAGE_DETECTION("lobby.language_detection"),

    SPAM("lobby.spam");

    private final String path;

    Text(final String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
