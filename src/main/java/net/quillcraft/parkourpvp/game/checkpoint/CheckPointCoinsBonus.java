package net.quillcraft.parkourpvp.game.checkpoint;

import java.util.Arrays;

public enum CheckPointCoinsBonus{

    BONUS_FIRST(1, 0), BONUS_SECOND(2, 0),  BONUS_THIRD(3, 0);

    private final int position;
    private int additionalCoins;

    CheckPointCoinsBonus(int position, int additionalCoins){
        this.position = position;
        this.additionalCoins = additionalCoins;
    }

    public int getPosition(){
        return position;
    }

    public int getAdditionalCoins(){
        return additionalCoins;
    }

    public void setAdditionalCoins(int additionalCoins){
        this.additionalCoins = additionalCoins;
    }

    public static int getBonus(int place){
        return Arrays.stream(values()).filter(checkPointCoinsBonus -> checkPointCoinsBonus.getPosition() == place).findFirst().get().getAdditionalCoins();
    }
}
