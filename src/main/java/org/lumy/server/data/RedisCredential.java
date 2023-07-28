package org.lumy.server.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.apache.logging.log4j.Level;
import org.lumy.Lumy;
import org.lumy.api.utils.FileUtils;

import reactor.util.annotation.Nullable;

import java.io.File;

public record RedisCredential(String ip, String clientName, String password, int database, int port) {

    @JsonIgnore
    public String getAdress(){
        return "redis://"+ip+":"+port;
    }

    @Nullable
    static RedisCredential getRedisCrendential(){
        try{
            final File configFile = FileUtils.getFileFromResource("config-redis.yml");

            return new ObjectMapper(new YAMLFactory()).readValue(configFile, RedisCredential.class);
        }catch(Exception e){
            Lumy.logger.log(Level.ERROR, e.getMessage(), e);
        }
        return null;
    }

}
