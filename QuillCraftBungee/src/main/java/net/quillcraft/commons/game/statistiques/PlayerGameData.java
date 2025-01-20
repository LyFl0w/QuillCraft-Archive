package net.quillcraft.commons.game.statistiques;

import java.util.UUID;

public class PlayerGameData {

    protected final String mapName, playerName;
    protected final UUID uuid;
    protected boolean hasWin;

    public PlayerGameData(UUID uuid, String playerName, String mapName, boolean hasWin) {
        this.mapName = mapName;
        this.playerName = playerName;
        this.uuid = uuid;
        this.hasWin = hasWin;
    }

    public PlayerGameData(UUID uuid, String playerName, String mapName) {
        this(uuid, playerName, mapName, false);
    }

    public String getMapName() {
        return mapName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean hasWin() {
        return hasWin;
    }

    public void setWin() {
        this.hasWin = true;
    }
}
