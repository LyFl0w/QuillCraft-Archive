package net.quillcraft.core.data.management.sql;

public class DatabaseCredentials{

    private final String host, user, pass, dbName;
    private final int port;

    protected DatabaseCredentials(String host, String user, String pass, String dbName, int port){
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.dbName = dbName;
        this.port = port;
    }

    protected String getAdress(){
        return "jdbc:mysql://"+host+":"+port+"/"+dbName+"?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&characterEncoding=utf8";
    }

    protected String getUser(){
        return user;
    }

    protected String getPass(){
        return pass;
    }
}
