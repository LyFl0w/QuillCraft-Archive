package org.lumy;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.lumy.api.utils.FileUtils;
import org.lumy.server.LumyServer;

public class Lumy {

    public final static Logger logger = LogManager.getLogger("Lumy");

    public static void main(String[] args) {
        try {
            new LumyServer(FileUtils.getObjectFromYamlFile(FileUtils.getFileFromResource("config-lumy.yml"), Lumy.Configuration.class));
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public record Configuration(String ip, int port) {}

}