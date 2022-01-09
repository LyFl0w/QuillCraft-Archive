package org.lumy;

import org.lumy.api.utils.FileUtils;
import org.lumy.server.LumyServer;
import org.lumy.server.data.RedisManager;
import org.apache.logging.log4j.LogManager;

public class Lumy{

    public static void main(String[] args){
        try{
            RedisManager.TEXT.getRedisAccess().init();
            new LumyServer(FileUtils.getObjectFromYamlFile(
                    FileUtils.getFileFromResource( "config-lumy.yml"), Lumy.Configuration.class));
        }catch(Exception e){
            LogManager.getLogger("lumy").error(e.getMessage(), e);
        }
    }
    
    public record Configuration(String ip, int port){}

}
