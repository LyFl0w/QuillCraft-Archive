package net.quillcraft.bungee.data.management.redis;

public record RedisCredential(String ip, String clientName, String password, int database, int port) {

    public String getAdress(){
        return "redis://"+ip+":"+port;
    }

}
