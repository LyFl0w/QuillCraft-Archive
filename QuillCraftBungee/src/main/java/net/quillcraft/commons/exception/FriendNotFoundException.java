package net.quillcraft.commons.exception;

import java.util.UUID;

public class FriendNotFoundException extends Exception {

    public FriendNotFoundException(UUID uuid) {
        super("The friends of ("+uuid.toString()+") was not found");
    }

}
