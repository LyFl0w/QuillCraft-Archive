package net.quillcraft.highblock.challenge.mod.block;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.challenge.PlayerChallengeProgress;
import net.quillcraft.highblock.challenge.Reward;
import net.quillcraft.highblock.challenge.type.MaterialChallenge;
import net.quillcraft.highblock.manager.ChallengeManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class RemoveBlockChallenge extends MaterialChallenge<BlockBreakEvent> {

    public RemoveBlockChallenge(HighBlock skyblock, int id, Difficulty difficulty, List<Integer> linkedChallengeID, List<Integer> counterList, List<List<Material>> elementsCounter, Reward reward, int slot, Material material, String name, String... description) {
        super(skyblock, id, difficulty, Type.REMOVE_BLOCK, linkedChallengeID, counterList, elementsCounter, reward, slot, material, name, description);
    }

    public RemoveBlockChallenge(HighBlock skyblock, int id, Difficulty difficulty, List<Integer> counterList, List<List<Material>> elementsCounter, Reward reward, int slot, Material material, String name, String... description) {
        this(skyblock, id, difficulty, Collections.emptyList(), counterList, elementsCounter, reward, slot, material, name, description);
    }

    @Override
    protected void onEvent(BlockBreakEvent event, Player player, PlayerChallengeProgress playerChallengeProgress) throws SQLException {
        final Material material = event.getBlock().getType();
        if(!challengeProgress.isValidElement(material)) return;
        challengeProgress.incrementCounter(player, 1, material);
    }

    public static class ListenerEvent implements Listener {

        private final List<RemoveBlockChallenge> challenges;
        public ListenerEvent(ChallengeManager challengeManager) {
            this.challenges = Collections.unmodifiableList((List<RemoveBlockChallenge>) challengeManager.getChallengesByType(Type.REMOVE_BLOCK));
        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockPlace(BlockBreakEvent event) {
            challenges.forEach(removeBlockChallenge -> removeBlockChallenge.onEventTriggered(event.getPlayer(), event));
        }

    }

}
