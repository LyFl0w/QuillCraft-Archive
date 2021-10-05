package net.quillcraft.bungee.text;

public enum TextList {

    SERVER_DESCRIPTION("server.description");

    private final String path;

    TextList(final String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }

}
