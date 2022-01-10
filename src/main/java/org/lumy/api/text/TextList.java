package org.lumy.api.text;

public enum TextList implements TextBase{

    //ITEMS INVENTORY PART
    ITEMS_INVENTORY_LOBBY_PARKOURPVP_LORE("items_inventory.lobby.parkourpvp.lore"),


    //TABLIST PART
    TABLIST_DEFAULT("tablist.default"),


    //TITLE PART
    TITLE_LOBBY_JOIN("title.lobby.join"),


    // SERVER PART
    SERVER_DESCRIPTION("server.description");

    private final String path;

    TextList(String path){
        this.path = path;
    }

    @Override
    public String getPath(){
        return path;
    }

}
