package net.quillcraft.parkourpvp.status;

public enum InGameStatus{

    WAIT_LOBBY, WAITING_BEFORE_JUMP, JUMP, WAITING_AFTER_JUMP, WAITING_BEFORE_PVP, PVP, WAITING_AFTER_PVP, END;

    public boolean actualInGameStatusIs(InGameStatus inGameStatus) {
        return this == inGameStatus;
    }

}
