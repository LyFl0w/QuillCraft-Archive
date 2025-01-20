package net.quillcraft.core.utils;

import org.bukkit.event.block.Action;

import java.util.Arrays;

public enum ActionUtils {

    RIGHT(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK), LEFT(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK);

    private final Action[] actions;

    ActionUtils(Action... actions) {
        this.actions = actions;
    }

    public static boolean hasRight(Action action) {
        return RIGHT.hasAction(action);
    }

    public static boolean hasLeft(Action action) {
        return LEFT.hasAction(action);
    }

    private boolean hasAction(Action newAction) {
        return Arrays.asList(actions).contains(newAction);
    }

}