package net.quillcraft.core.utils.builders.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class TeamBuilder{

    private HashMap<Team.Option, Team.OptionStatus> teamOptions;
    private String name, displayName, prefix, suffix;
    private boolean allowFriendlyFire, canSeeFriendlyInvisibles;
    private ChatColor color;

    public TeamBuilder(String name, String displayName){
        this(name, displayName, "", "", true, false, ChatColor.WHITE, new HashMap<>());
    }

    public TeamBuilder(String name, String displayName, String prefix, String suffix, boolean allowFriendlyFire, boolean canSeeFriendlyInvisibles, ChatColor color, HashMap<Team.Option, Team.OptionStatus> teamOptions){
        this.name = name;
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.allowFriendlyFire = allowFriendlyFire;
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        this.color = color;
        this.teamOptions = teamOptions;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDisplayName(){
        return displayName;
    }

    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }

    public String getPrefix(){
        return prefix;
    }

    public void setPrefix(String prefix){
        this.prefix = prefix;
    }

    public String getSuffix(){
        return suffix;
    }

    public void setSuffix(String suffix){
        this.suffix = suffix;
    }

    public boolean isAllowFriendlyFire(){
        return allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean allowFriendlyFire){
        this.allowFriendlyFire = allowFriendlyFire;
    }

    public boolean isCanSeeFriendlyInvisibles(){
        return canSeeFriendlyInvisibles;
    }

    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles){
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
    }

    public ChatColor getColor(){
        return color;
    }

    public void setColor(ChatColor color){
        this.color = color;
    }

    public HashMap<Team.Option, Team.OptionStatus> getTeamOptions(){
        return teamOptions;
    }

    public void setTeamOptions(HashMap<Team.Option, Team.OptionStatus> teamOptions){
        this.teamOptions = teamOptions;
    }
}
