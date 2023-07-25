package net.quillcraft.core.utils.builders;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardBuilder {

    private final static int maxLine = 15;
    private final static int maxColorLength = 16;

    private final Scoreboard scoreboard;
    private final ArrayList<Team> teamList;
    private final Objective objective;

    public ScoreboardBuilder(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.teamList = new ArrayList<>();
        this.objective = scoreboard.registerNewObjective(title, Criteria.DUMMY, title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for(int i=0; i<maxLine; i++) {
            String name = generateRandomColor(maxColorLength);
            while(scoreboard.getTeam(name) != null) {
                name = generateRandomColor(maxColorLength);
            }

            final Team team = this.scoreboard.registerNewTeam(name);
            team.addEntry(name);
            this.teamList.add(team);
        }
    }

    public ScoreboardBuilder setLines(String... lines) {
        if(lines.length > maxLine) throw new RuntimeException("There can be no more than "+maxLine+" lines on a scoreboard.");

        // Clear lines of score
        this.scoreboard.resetScores(getTitle());

        final int lineSize = lines.length-1;
        for(int i=0; i<=lineSize; i++) {
            final int index = lineSize-i;
            final Team team = this.teamList.get(index);
            team.setPrefix(lines[i]);

            objective.getScore(team.getName()).setScore(index);
        }

        return this;
    }

    public ScoreboardBuilder setLines(List<String> lines, List<Integer> linesIndex) {
        final int size = lines.size();

        if(size != linesIndex.size()) throw new RuntimeException("The number of lines doesn't correspond to the number of indexes.");
        if(size > maxLine) throw new RuntimeException("There can be no more than "+maxLine+" lines on a scoreboard.");

        for(int i=0; i<size; i++) setLine(lines.get(i), linesIndex.get(i));

        return this;
    }

    public ScoreboardBuilder setLine(String line, int index) {
        if(index >= maxLine) throw new RuntimeException("There can be no more than "+maxLine+" lines on a scoreboard.");

        final Team team = this.teamList.get(index);
        team.setPrefix(line);

        // Draw line if not already drawn
        final Score score = objective.getScore(team.getName());
        if(!score.isScoreSet()) score.setScore(index);

        return this;
    }

    public String getTitle() {
        return this.objective.getDisplayName();
    }

    public ScoreboardBuilder setTitle(String title) {
        this.objective.setDisplayName(title);

        return this;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    private static String generateRandomColor(int colorLength) {
        final StringBuilder stringBuilder = new StringBuilder();
        final ChatColor[] chatColors = ChatColor.values();
        final int length = chatColors.length;

        for(int i=0; i<=colorLength-1; i++) {
            stringBuilder.append(chatColors[new SecureRandom().nextInt(length-1)]).append(ChatColor.RESET);
        }

        return stringBuilder.toString();
    }

}
