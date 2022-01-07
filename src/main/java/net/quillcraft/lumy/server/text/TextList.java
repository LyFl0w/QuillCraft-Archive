package net.quillcraft.lumy.server.text;

public enum TextList {

    ;

    private final String path;

    TextList(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
