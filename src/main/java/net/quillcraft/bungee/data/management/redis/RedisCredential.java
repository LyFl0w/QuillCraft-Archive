package net.quillcraft.bungee.data.management.redis;

public record RedisCredential(String ip, String clientName, String password, int database, int port) {

    public final String getPassword(){
        return password;
    }

    public final String getClientName(){
        return clientName;
    }

    public final int getDatabase(){
        return database;
    }

    public final String getAdress(){
        return "redis://"+ip+":"+port;
    }

}
