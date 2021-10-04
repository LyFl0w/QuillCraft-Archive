package net.quillcraft.core.event.action;

import org.bukkit.event.block.Action;

import java.util.Arrays;

public enum ActualAction {

    RIGHT(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK),
    LEFT(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK);

    private final Action[] actions;
    ActualAction(Action... actions){
        this.actions = actions;
    }

    private boolean hasAction(Action newAction){
        return Arrays.asList(actions).contains(newAction);
    }

    public static boolean hasRight(Action action){
        return RIGHT.hasAction(action);
    }

    public static boolean hasLeft(Action action){
        return LEFT.hasAction(action);
    }

}
