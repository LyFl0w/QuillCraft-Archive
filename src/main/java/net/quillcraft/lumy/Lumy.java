package net.quillcraft.lumy;

import net.quillcraft.lumy.client.LumyClient;
import net.quillcraft.lumy.server.LumyServer;
import net.quillcraft.lumy.server.data.RedisManager;
import net.quillcraft.lumy.utils.FileUtils;

public class Lumy{

    public static void main(String[] args){
        if(args.length > 1) return;

        try{
            Lumy.Configuration lumyConfiguration = FileUtils.getObjectFromYamlFile(
                    FileUtils.getFileFromResource( "config-lumy.yml"), Lumy.Configuration.class);

            switch(args[0]){
                case "client" -> new LumyClient(lumyConfiguration);
                case "server" -> {
                    RedisManager.TEXT.getRedisAccess().init();
                    new LumyServer(lumyConfiguration);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public record Configuration(String ip, int port){}

}
