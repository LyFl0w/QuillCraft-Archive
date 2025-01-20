package net.quillcraft.lobby.manager;

import net.quillcraft.core.utils.builders.ScoreboardBuilder;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class ScoreboardManager {

    private final HashMap<UUID, ScoreboardBuilder> scoreboardBuilderHashMap;

    public ScoreboardManager() {
        this.scoreboardBuilderHashMap = new HashMap<>();
    }

    public ScoreboardBuilder getScoreboardBuilder(Player player) {
        return scoreboardBuilderHashMap.get(player.getUniqueId());
    }

    public void addScoreboardBuilder(Player player, ScoreboardBuilder scoreboardBuilder) {
        final UUID uuid = player.getUniqueId();
        if(!scoreboardBuilderHashMap.containsKey(uuid)) {
            scoreboardBuilderHashMap.put(uuid, scoreboardBuilder);
        } else {
            scoreboardBuilderHashMap.replace(uuid, scoreboardBuilder);
        }
        player.setScoreboard(scoreboardBuilder.getScoreboard());
    }

    public Collection<ScoreboardBuilder> getAllScoreboardBuilders() {
        return scoreboardBuilderHashMap.values();
    }

    public void removeScoreboard(UUID uuid) {
        scoreboardBuilderHashMap.remove(uuid);
    }

}
