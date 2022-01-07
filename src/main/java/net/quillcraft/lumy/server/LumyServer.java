package net.quillcraft.lumy.server;

import net.quillcraft.lumy.Lumy;
import net.quillcraft.lumy.server.manager.LanguageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LumyServer{

    private final int port;
    private final Logger logger;

    private final ScheduledExecutorService scheduleur;
    private ScheduledFuture<?> scheduledFuture;

    private long lastUpdate = 0L;

    public LumyServer(Lumy.Configuration lumyConfiguration){
        this.logger = LogManager.getLogger("Lumy");
        this.port = lumyConfiguration.port();
        this.scheduleur = Executors.newScheduledThreadPool(1);
        this.scheduledFuture = scheduleTask();

        LanguageManager.initAllLanguage();
        try{
            start();
        }catch(Exception e){
            logger.fatal(e);
            System.exit(0);
        }
    }

    private ScheduledFuture<?> scheduleTask(){
        return scheduleur.scheduleWithFixedDelay(this::updateDataBase, 0, 2, TimeUnit.HOURS);
    }

    private void restartScheduleTask(){
        scheduledFuture.cancel(false);
        this.scheduledFuture = scheduleTask();
    }

    private void start() throws Exception{
        final ServerSocket serverSocket = new ServerSocket(port);
        logger.info("Server start on port : "+port);
        while(true){
            try{
                final Socket clientSocket = serverSocket.accept();

                new ClientConnection(clientSocket, new DataInputStream(clientSocket.getInputStream())).start();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private class ClientConnection extends Thread{

        private final Socket clientSocket;
        private final DataInputStream dataInputStream;
        private final ScheduledExecutorService scheduleur;
        private boolean exit = false;

        public ClientConnection(Socket clientSocket, DataInputStream dataInputStream){
            this.clientSocket = clientSocket;
            this.dataInputStream = dataInputStream;
            //Auto Destroy if inactif
            this.scheduleur = Executors.newScheduledThreadPool(0);
            scheduleur.schedule(this::stopThreadAction, 1, TimeUnit.MINUTES);
        }

        public void run(){
            logger.info("new client : "+getFullAdress());
            while(!exit){
                try{
                    final String message = dataInputStream.readUTF();

                    switch(message.toLowerCase()){
                        case "update" -> {
                            logger.info("update database requested from : "+getFullAdress());
                            restartScheduleTask();
                            updateDataBase();
                        }

                        case "ping" -> {
                            logger.info("ping request from : "+getFullAdress());
                            final DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                            dataOutputStream.writeUTF("pong");
                            dataOutputStream.close();
                        }
                    }

                    break;
                }catch(IOException e){
                    logger.info("Connection lost : "+getFullAdress());
                    return;
                }
            }
            scheduleur.shutdownNow();
            try{
                logger.info("Connection closed : "+getFullAdress());
                clientSocket.close();
                dataInputStream.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        private void stopThreadAction(){
            logger.warn("A one-minute delay was exceeded with the customer : "+getFullAdress());
            exit = true;
            try{
                dataInputStream.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        private String getFullAdress(){
            return clientSocket.getInetAddress()+":"+clientSocket.getPort();
        }

    }

    private void updateDataBase(){
        if(System.currentTimeMillis()-lastUpdate >= 300000L) return;
        lastUpdate = System.currentTimeMillis();

        LanguageManager.getLastLanguagesModifiedTime(3, TimeUnit.HOURS).forEach(LanguageManager::updateTexteRedis);
    }


}