package net.quillcraft.commons.game;

public enum GameEnum{

    PARKOUR_PVP_SOLO("Parkour PvP (solo)");

    private final String gameName;

    GameEnum(String gameName){
        this.gameName = gameName;
    }

    public String getGameName(){
        return gameName;
    }

}
