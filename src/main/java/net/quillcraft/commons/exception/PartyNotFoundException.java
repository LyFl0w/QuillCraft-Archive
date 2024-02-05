package net.quillcraft.commons.exception;

import java.util.UUID;

public class PartyNotFoundException extends Exception {

    public PartyNotFoundException(UUID uuid) {
        super("The party of (" + uuid.toString() + ") was not found");
    }

}