package net.quillcraft.lumy.api;

import net.quillcraft.lumy.api.utils.FileUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LumyClient{

    private final LinkedList<String> result;

    public LumyClient(String[] args, Logger logger, File dataFolder){
        this.result = new LinkedList<>();

        try{
            final LumyConfiguration lumyConfiguration = FileUtils.getObjectFromYamlFile(
                    FileUtils.getFileFromResource(dataFolder, "config-lumy.yml"), LumyConfiguration.class);
            start(lumyConfiguration.name(), args, logger, lumyConfiguration.ip(), lumyConfiguration.port());
        }catch(IOException e){
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void start(String name, String[] actions, Logger logger, String ip, int port){
        try{
            logger.info("Attempting to connect to the Lumy server");

            if(Arrays.stream(actions).filter(action -> action.startsWith("name:")).count() > 1) {
                logger.severe("You can't have multiple names");
                return;
            }

            final Socket socket = new Socket(InetAddress.getByName(ip), port);

            try{
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

                if(name != null && !name.isBlank()) {
                    printWriter.println("name:"+name);
                    logger.info("Successful connection to Lumy server as "+name);
                } else {
                    logger.info("Successful connection to Lumy server");
                }

                loop: for(String action : actions) {
                    printWriter.println(action.toLowerCase());

                    // obtaining out streams
                    logger.info(action+" request sent to Lumy server");

                    if(action.startsWith("name:")) continue;

                    switch(action.toLowerCase()) {
                        case "update" -> {}

                        default -> {
                            final String respond = bufferedReader.readLine();

                            if(respond.startsWith("error:")) {
                                logger.severe(respond);
                                break loop;
                            }
                            if(respond.equals("closed")) break loop;

                            result.addLast(respond);
                        }
                    }
                }

                printWriter.close();
                bufferedReader.close();

                socket.close();
            }catch(IOException e){
                logger.log(Level.SEVERE, e.getMessage(), e);
            }

        }catch(UnknownHostException e){
            logger.log(Level.SEVERE, "Unknown Host", e);
        }catch(IOException e){
            logger.log(Level.SEVERE, "Connexion Refused", e);
        }
    }

    public int readInt() {
        return Integer.parseInt(result.poll());
    }

    public byte[] readBytes() {
        return Base64.getDecoder().decode(result.poll());
    }

    public String read() {
        return result.poll();
    }

    private record LumyConfiguration(String name, String ip, int port){}

}