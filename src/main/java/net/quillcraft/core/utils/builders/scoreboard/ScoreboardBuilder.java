package net.quillcraft.core.utils.builders.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Objects;

public class ScoreboardBuilder {

    private final JavaPlugin javaPlugin;

    private final Scoreboard scoreboard;
    private final ArrayList<ObjectiveBuilder> objectiveBuilders = new ArrayList<>();
    private final ArrayList<String> players = new ArrayList<>();

    public ScoreboardBuilder(JavaPlugin javaPlugin){
        this.javaPlugin = javaPlugin;
        scoreboard = javaPlugin.getServer().getScoreboardManager().getNewScoreboard();
    }

    public void addPlayer(Player player){
        players.add(player.getName());
    }

    public void removePlayer(Player player){
        players.remove(player.getName());
    }

    public ScoreboardBuilder addObjective(ObjectiveBuilder objectiveBuilder){
        addObjectiveOnScoreboard(objectiveBuilder);

        objectiveBuilders.add(objectiveBuilder);

        return this;
    }

    public ScoreboardBuilder updateScore(String objectiveName, int index, String score){
        final ObjectiveBuilder objectiveBuilder = objectiveBuilders.stream().parallel().filter(objectivesBuilder -> objectivesBuilder.getName()
                .equals(objectiveName)).findFirst().get();
        objectiveBuilder.removeScore(index).addScore(index, score);

        scoreboard.getObjective(objectiveName).unregister();

        addObjectiveOnScoreboard(objectiveBuilder);

        return this;
    }

    public void updateScoreboard(){
        players.stream().parallel().forEach(playersName -> {
            final Player player = javaPlugin.getServer().getPlayerExact(playersName);
            if(Objects.requireNonNull(player).isOnline()){
                player.setScoreboard(scoreboard);
            }
        });
    }

    private void addObjectiveOnScoreboard(ObjectiveBuilder objectiveBuilder){
        final Objective objective = scoreboard.registerNewObjective(objectiveBuilder.getName(), "dummy", objectiveBuilder.getDisplayName());
        objective.setDisplaySlot(objectiveBuilder.getDisplaySlot());

        objectiveBuilder.getScoreBuilders().stream().parallel().forEach(scoreBuilder -> objective.getScore(scoreBuilder.score()).setScore(scoreBuilder.index()));
    }
}
