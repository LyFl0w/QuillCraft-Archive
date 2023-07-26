package net.quillcraft.core.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PlayerUtils {

    public static void resetDefaultParmetersPlayer(Player player) {
        resetDefaultParmetersPlayer(player, GameMode.ADVENTURE);
    }

    public static void resetDefaultParmetersPlayer(Player player, GameMode gameMode) {
        player.setGameMode(gameMode);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setTotalExperience(0);
        player.setExp(0.0f);
    }

}
