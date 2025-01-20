package net.quillcraft.bungee.data.sql;

public record DatabaseCredentials(String host, String user, String pass, String dbName, int port) {

    public String toURI() {
        return "jdbc:mysql://"+host+":"+port+"/"+dbName+"?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&characterEncoding=utf8";
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
