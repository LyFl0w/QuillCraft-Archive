package net.quillcraft.core.utils.builders.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Objects;

public class ScoreboardBuilder{

    private final JavaPlugin javaPlugin;

    private final Scoreboard scoreboard;
    private final ArrayList<ObjectiveBuilder> objectiveBuilders = new ArrayList<>();
    private final ArrayList<Team> teams = new ArrayList<>();
    private final ArrayList<String> playersName = new ArrayList<>();

    public ScoreboardBuilder(JavaPlugin javaPlugin){
        this.javaPlugin = javaPlugin;
        scoreboard = javaPlugin.getServer().getScoreboardManager().getNewScoreboard();
    }

    public void addPlayer(Player player){
        playersName.add(player.getName());
    }

    public void removePlayer(Player player){
        playersName.remove(player.getName());
    }

    public ScoreboardBuilder addObjective(ObjectiveBuilder objectiveBuilder){
        addObjectiveOnScoreboard(objectiveBuilder);

        objectiveBuilders.add(objectiveBuilder);
        return this;
    }

    public ScoreboardBuilder addTeam(TeamBuilder teamBuilder){
        teams.add(addTeamOnScoreboard(teamBuilder));

        return this;
    }

    public ScoreboardBuilder removeTeam(String teamName){
        final Team team = getTeamByName(teamName);
        team.unregister();
        teams.remove(team);

        return this;
    }

    public ScoreboardBuilder addPlayerTeam(String teamName, String playerName){
        getTeamByName(teamName).addEntry(playerName);

        return this;
    }

    public ScoreboardBuilder removePlayerTeam(String teamName, String playerName){
        getTeamByName(teamName).removeEntry(playerName);

        return this;
    }

    private Team getTeamByName(String teamName){
        return teams.stream().parallel().filter(teams -> teams.getName().equalsIgnoreCase(teamName)).findFirst().get();
    }

    public ScoreboardBuilder updateScore(String objectiveName, int index, String score){
        final ObjectiveBuilder objectiveBuilder = objectiveBuilders.stream().parallel()
                .filter(objectivesBuilder -> objectivesBuilder.getName().equals(objectiveName))
                .findFirst().get();

        objectiveBuilders.remove(objectiveBuilder);
        scoreboard.getObjective(objectiveName).unregister();

        objectiveBuilder.removeScore(index).addScore(index, score);

        addObjective(objectiveBuilder);
        return this;
    }

    public void updateScoreboard(){
        playersName.stream().parallel().forEach(playersName -> {
            final Player player = javaPlugin.getServer().getPlayerExact(playersName);
            Objects.requireNonNull(player).setScoreboard(scoreboard);
        });
    }

    private void addObjectiveOnScoreboard(ObjectiveBuilder objectiveBuilder){
        final Objective objective = scoreboard.registerNewObjective(objectiveBuilder.getName(), "dummy",
                objectiveBuilder.getDisplayName());
        objective.setDisplaySlot(objectiveBuilder.getDisplaySlot());

        objectiveBuilder.getScoreBuilders()
                .forEach(scoreBuilder -> objective.getScore(scoreBuilder.score()).setScore(scoreBuilder.index()));
    }

    private Team addTeamOnScoreboard(TeamBuilder teamBuilder){
        final Team team = scoreboard.registerNewTeam(teamBuilder.getName());

        team.setCanSeeFriendlyInvisibles(teamBuilder.isCanSeeFriendlyInvisibles());
        team.setAllowFriendlyFire(teamBuilder.isAllowFriendlyFire());
        team.setColor(teamBuilder.getColor());
        team.setPrefix(teamBuilder.getPrefix());
        team.setSuffix(teamBuilder.getSuffix());
        team.setDisplayName(teamBuilder.getDisplayName());
        teamBuilder.getTeamOptions().forEach(team::setOption);

        return team;
    }

    public ArrayList<String> getPlayersName(){
        return (ArrayList<String>) playersName.clone();
    }

}
