package net.quillcraft.lobby.game;

import net.quillcraft.commons.game.GameEnum;

public enum GameItemToGameEnum{

        IRON_BOOTS(GameEnum.PARKOUR_PVP_SOLO);

        private final GameEnum gameEnum;

        GameItemToGameEnum(GameEnum gameEnum){
            this.gameEnum = gameEnum;
        }

        public GameEnum getGameEnum(){
            return gameEnum;
        }

}