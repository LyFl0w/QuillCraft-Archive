package net.quillcraft.core.utils.builders.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveBuilder {

    private String name, displayName;
    private DisplaySlot displaySlot;

    private final List<ScoreBuilder> scoreBuilders;

    public ObjectiveBuilder(String name, String displayName, DisplaySlot displaySlot){
        this.name = name;
        this.displayName = displayName;
        this.displaySlot = displaySlot;
        this.scoreBuilders = new ArrayList<>();
    }

    public ObjectiveBuilder addScore(int index, String score){
        scoreBuilders.add(new ScoreBuilder(index, score));

        return this;
    }

    public ObjectiveBuilder removeScore(int index){
        ScoreBuilder scoreBuilder = scoreBuilders.stream().filter(oldScoreBuilder -> oldScoreBuilder.index() == index).findFirst().get();
        scoreBuilders.remove(scoreBuilder);

        return this;
    }

    public String getName(){
        return name;
    }

    public ObjectiveBuilder setName(String name){
        this.name = name;

        return this;
    }

    public String getDisplayName(){
        return displayName;
    }

    public ObjectiveBuilder setDisplayName(String displayName){
        this.displayName = displayName;

        return this;
    }

    public DisplaySlot getDisplaySlot(){
        return displaySlot;
    }

    public ObjectiveBuilder setDisplaySlot(DisplaySlot displaySlot){
        this.displaySlot = displaySlot;

        return this;
    }

    protected List<ScoreBuilder> getScoreBuilders(){
        return scoreBuilders;
    }

    protected record ScoreBuilder(int index, String score){}
}
