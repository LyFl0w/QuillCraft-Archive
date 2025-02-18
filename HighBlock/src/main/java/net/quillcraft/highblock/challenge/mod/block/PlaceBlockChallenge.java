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
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PlaceBlockChallenge extends MaterialChallenge<BlockPlaceEvent> {

    public PlaceBlockChallenge(HighBlock highblock, int id, Difficulty difficulty, List<Integer> linkedChallengeID, List<Integer> counterList, List<List<Material>> elementsCounter, Reward reward, int slot, Material material, String name, String... description) {
        super(highblock, id, difficulty, Type.PLACE_BLOCK, linkedChallengeID, counterList, elementsCounter, reward, slot, material, name, description);
    }

    public PlaceBlockChallenge(HighBlock highblock, int id, Difficulty difficulty, Type type, List<Integer> counterList, List<List<Material>> elementsCounter, Reward reward, int slot, Material material, String name, String... description) {
        this(highblock, id, difficulty, Collections.emptyList(), counterList, elementsCounter, reward, slot, material, name, description);
    }

    @Override
    protected void onEvent(BlockPlaceEvent event, Player player, PlayerChallengeProgress playerChallengeProgress) throws SQLException {
        final Material material = event.getBlockPlaced().getType();
        if (challengeProgress.isNotValidElement(material)) return;
        challengeProgress.incrementCounter(player, 1, material);
    }

    public static class ListenerEvent implements Listener {

        private final List<PlaceBlockChallenge> challenges;

        public ListenerEvent(ChallengeManager challengeManager) {
            this.challenges = Collections.unmodifiableList((List<PlaceBlockChallenge>) challengeManager.getChallengesByType(Type.PLACE_BLOCK));
        }

        @EventHandler(ignoreCancelled = true)
        public void onBlockPlace(BlockPlaceEvent event) {
            challenges.stream().parallel().forEach(placeBlockChallenge -> placeBlockChallenge.onEventTriggered(event.getPlayer(), event));
        }

    }

}
