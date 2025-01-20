package net.quillcraft.core.exception;

public class TaskOverflowException extends Exception {

    public TaskOverflowException() {
        super("A task is already in use");
    }

}
