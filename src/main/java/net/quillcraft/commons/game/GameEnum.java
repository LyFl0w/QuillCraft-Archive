package net.quillcraft.commons.game;

import net.quillcraft.commons.game.statistiques.PlayerGameStatistique;
import net.quillcraft.commons.game.statistiques.parkourpvp.PlayerParkourPvPStatistique;

public enum GameEnum {

    PARKOUR_PVP_SOLO("Parkour PvP (solo)", PlayerParkourPvPStatistique.class);

    private final String gameName;
    private final Class<? extends PlayerGameStatistique> gameStatistiqueClass;

    GameEnum(String gameName, Class<? extends PlayerGameStatistique> gameStatistiqueClass) {
        this.gameName = gameName;
        this.gameStatistiqueClass = gameStatistiqueClass;
    }

    public String getGameName() {
        return gameName;
    }

    public Class<? extends PlayerGameStatistique> getGameStatistiqueClass() {
        return gameStatistiqueClass;
    }
}
