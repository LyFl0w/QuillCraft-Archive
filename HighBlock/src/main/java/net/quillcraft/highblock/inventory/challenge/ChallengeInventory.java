package net.quillcraft.highblock.inventory.challenge;

import net.quillcraft.highblock.challenge.Challenge;
import net.quillcraft.highblock.manager.ChallengeManager;
import net.quillcraft.highblock.utils.builder.InventoryBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class ChallengeInventory {

    private ChallengeInventory() {
        throw new IllegalStateException("Inventory class");
    }

    public static Inventory getMenuChallengeInventory() {
        final InventoryBuilder inventoryBuilder = new InventoryBuilder(9, "§gChallenges - Menu");
        Arrays.stream(Challenge.Difficulty.values()).forEach(difficulty -> inventoryBuilder.setItem(difficulty.getSlot(), difficulty.getItemStack()));
        return inventoryBuilder.toInventory();
    }

    public static Inventory getChallengeInventory(ChallengeManager challengeManager, Player player, Challenge.Difficulty difficulty) {
        final InventoryBuilder inventoryBuilder = new InventoryBuilder(9, "§gChallenges - " + difficulty.getName());
        challengeManager.getChallengesByDifficulty(difficulty).stream().parallel().forEach(challenge ->
                inventoryBuilder.setItem(challenge.getSlot(), challenge.getRepresentation(player)));
        return inventoryBuilder.toInventory();
    }

}
