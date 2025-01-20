package net.quillcraft.lumy;

import net.quillcraft.lumy.api.utils.FileUtils;
import net.quillcraft.lumy.server.LumyServer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Lumy {

    public static final Logger logger = LogManager.getLogger("Lumy");

    public static void main(String[] args) {
        try {
            new LumyServer(FileUtils.getObjectFromYamlFile(FileUtils.getFileFromResource("config-lumy.yml"), Lumy.Configuration.class));
        } catch(Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
    }

    public record Configuration(String ip, int port) {}

}