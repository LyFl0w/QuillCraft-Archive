package org.lumy.api;

import org.lumy.api.utils.FileUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LumyClient{

    public LumyClient(Logger logger){
        try{
            final LumyConfiguration lumyConfiguration = FileUtils.getObjectFromYamlFile(
                    FileUtils.getFileFromResource("config-lumy.yml"), LumyConfiguration.class);
            start(logger, lumyConfiguration.ip(), lumyConfiguration.port());
        }catch(IOException e){
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void start(Logger logger, String ip, int port){
        try{
            logger.info("Attempting to connect to the Lumy server");
            // getting ip -> InetAddress.getByName(ip)
            // establish the connection with server port
            final Socket socket = new Socket(InetAddress.getByName(ip), port);

            logger.info("Successful connection to Lumy server");

            try{
                // obtaining out streams
                logger.info("Update request to Lumy server");

                final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF("update");

                logger.info("Request sent to Lumy server");

                socket.close();
                dataOutputStream.close();
            }catch(IOException e){
                logger.log(Level.SEVERE, e.getMessage(), e);
            }

        }catch(UnknownHostException e){
            logger.log(Level.SEVERE, "Unknown Host", e);
        }catch(IOException e){
            logger.log(Level.SEVERE, "Connexion Refused", e);
        }
    }

    private record LumyConfiguration(String ip, int port){}

}