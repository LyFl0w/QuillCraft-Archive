package net.quillcraft.highblock.challenge.mod;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.challenge.PlayerChallengeProgress;
import net.quillcraft.highblock.challenge.Reward;
import net.quillcraft.highblock.challenge.type.MaterialChallenge;
import net.quillcraft.highblock.manager.ChallengeManager;
import net.quillcraft.highblock.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class CraftItemChallenge extends MaterialChallenge<CraftItemEvent> {

    public CraftItemChallenge(HighBlock highblock, int id, Difficulty difficulty, List<Integer> linkedChallengeID, List<Integer> counterList, List<List<Material>> elementsCounter, Reward reward, int slot, Material material, String name, String... description) {
        super(highblock, id, difficulty, Type.CRAFT_ITEM, linkedChallengeID, counterList, elementsCounter, reward, slot, material, name, description);
    }

    public CraftItemChallenge(HighBlock highblock, int id, Difficulty difficulty, List<Integer> counterList, List<List<Material>> elementsCounter, Reward reward, int slot, Material material, String name, String... description) {
        this(highblock, id, difficulty, Collections.emptyList(), counterList, elementsCounter, reward, slot, material, name, description);
    }

    @Override
    protected void onEvent(CraftItemEvent event, Player player, PlayerChallengeProgress playerChallengeProgress) throws SQLException {
        final ItemStack itemStack = event.getRecipe().getResult();
        if (challengeProgress.isNotValidElement(itemStack.getType())) return;
        challengeProgress.incrementCounter(player, (event.isShiftClick()) ? InventoryUtils.getCraftedItemAmount(event.getInventory())
                : itemStack.getAmount(), itemStack.getType());
    }

    public static class ListenerEvent implements Listener {

        private final List<CraftItemChallenge> challenges;

        public ListenerEvent(ChallengeManager challengeManager) {
            this.challenges = Collections.unmodifiableList((List<CraftItemChallenge>) challengeManager.getChallengesByType(Type.CRAFT_ITEM));
        }

        @EventHandler(ignoreCancelled = true)
        public void onCraftItem(CraftItemEvent event) {
            challenges.stream().parallel().forEach(placeBlockChallenge -> placeBlockChallenge.onEventTriggered((Player) event.getWhoClicked(), event));
        }

    }

}
