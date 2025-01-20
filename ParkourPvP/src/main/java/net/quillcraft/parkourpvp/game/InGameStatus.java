package net.quillcraft.parkourpvp.game;

public enum InGameStatus{

    WAIT_LOBBY, WAITING_BEFORE_JUMP, JUMP, WAITING_AFTER_JUMP, WAITING_BEFORE_PVP, PVP, END;

    public boolean actualInGameStatusIs(InGameStatus inGameStatus) {
        return this == inGameStatus;
    }

}
