package net.quillcraft.bungee.data.redis;

public record RedisCredential(String ip, String clientName, String password, int database, int port) {

    public String getAdress() {
        return "redis://"+ip+":"+port;
    }

}
