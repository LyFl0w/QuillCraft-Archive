package net.quillcraft.lobby.text;

public enum TextList {

    LOBBY_AUTOTEXT("lobby.auto_text"),
    LORE_PARKOURPVP_TEXT("lobby.item.parkourpvp.lore");

    private final String path;

    TextList(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
