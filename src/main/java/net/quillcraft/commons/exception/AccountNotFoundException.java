package net.quillcraft.commons.exception;

import java.util.UUID;

public class AccountNotFoundException extends Exception{

    public AccountNotFoundException(UUID uuid){
        super("The account ("+uuid.toString()+") was not found");
    }

}
