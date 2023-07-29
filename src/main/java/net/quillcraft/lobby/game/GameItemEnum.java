package net.quillcraft.lobby.game;

import net.quillcraft.commons.game.GameEnum;

public enum GameItemEnum {

    IRON_BOOTS(GameEnum.PARKOUR_PVP_SOLO);

    private final GameEnum gameEnum;

    GameItemEnum(GameEnum gameEnum) {
        this.gameEnum = gameEnum;
    }

    public GameEnum getGameEnum() {
        return gameEnum;
    }

}