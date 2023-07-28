package org.lumy.server;

import org.apache.logging.log4j.Level;
import org.lumy.Lumy;
import org.lumy.api.utils.FileUtils;
import org.lumy.server.data.RedisManager;
import org.lumy.server.manager.LanguageManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LumyServer {

    private final int port;
    private final Logger logger;

    private final ScheduledExecutorService scheduleur;
    private ScheduledFuture<?> scheduledFuture;

    private long lastUpdate = 0L;

    public LumyServer(Lumy.Configuration lumyConfiguration) {
        this.logger = Lumy.logger;
        this.port = lumyConfiguration.port();
        this.scheduleur = Executors.newScheduledThreadPool(1);

        start();
    }

    private ScheduledFuture<?> scheduleTask(int initialDealay) {
        return scheduleur.scheduleWithFixedDelay(this::updateDataBase, initialDealay, 2, TimeUnit.HOURS);
    }

    private void restartScheduleTask() {
        scheduledFuture.cancel(false);
        this.scheduledFuture = scheduleTask(0);
    }

    private void start() {
        try(final ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server start on port : "+port);

            RedisManager.TEXT.getRedisAccess().init();
            this.scheduledFuture = scheduleTask(2);

            updateDataBaseNow();

            while(true) {
                try {
                    final Socket clientSocket = serverSocket.accept();

                    new ClientConnection(clientSocket, new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))).start();
                } catch(Exception error) {
                    Lumy.logger.log(Level.WARN, error.getMessage(), error);
                }
            }
        } catch(IOException error) {
            Lumy.logger.log(Level.FATAL, error.getMessage(), error);
        }
        RedisManager.TEXT.getRedisAccess().close();
        System.exit(0);
    }

    private class ClientConnection extends Thread {

        private final Socket clientSocket;
        private final BufferedReader bufferedReader;
        private final ScheduledExecutorService scheduleur;
        private boolean exit = false;

        private String name = "";
        private boolean skipName = false;
        private final int delay = 30;

        public ClientConnection(Socket clientSocket, BufferedReader bufferedReader) {
            this.clientSocket = clientSocket;
            this.bufferedReader = bufferedReader;
            //Auto Destroy if inactif
            this.scheduleur = Executors.newScheduledThreadPool(0);
            scheduleur.schedule(this::stopThreadAction, delay, TimeUnit.SECONDS);

            System.out.println("Start connection from : "+getFullAddress());
        }

        public void run() {
            try {
                final PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

                while(!exit) {
                    try {
                        final String request = bufferedReader.readLine();

                        if(request == null) break;

                        // get client name
                        if(request.startsWith("name:")) {
                            if(skipName) {
                                exit = true;

                                printWriter.println("error: The client name have to be defined at the start");
                                break;
                            }

                            name = request.substring(5);
                            if(name.isBlank()) {
                                exit = true;

                                printWriter.println("error: The client name can't be blank");
                                break;
                            }

                            skipName = true;
                            Lumy.logger.info("new client "+getClientName()+": "+getFullAddress());
                            continue;
                        }

                        // skip if client send name after other request
                        if(!skipName) {
                            skipName = true;
                            Lumy.logger.info("new client : "+getFullAddress());
                        }

                        Lumy.logger.info(request+" request from "+getClientName()+": "+getFullAddress());
                        switch(request) {
                            case "update" -> restartScheduleTask();

                            case "ping" -> printWriter.println("pong");

                            case "data_access" -> {
                                if(isLocalIP()) {
                                    final File file = FileUtils.getFileFromResource("data_access.yml");
                                    printWriter.println(new String(Base64.getEncoder().encode(Files.readAllBytes(file.toPath())), StandardCharsets.UTF_8));
                                } else {
                                    final String name = getClientName();
                                    Lumy.logger.info((name.isEmpty() ? "" : name+" ")+getFullAddress()+" try to get data_access.yml data");
                                    exit = true;

                                    sendDefaultRequest(printWriter, request);
                                }
                            }

                            case "absolute_path_data_access" -> {
                                if(isLocalIP()) {
                                    printWriter.println(FileUtils.getFileFromResource("data_access.yml").getAbsolutePath());
                                } else {
                                    final String name = getClientName();
                                    Lumy.logger.info((name.isEmpty() ? "" : name+" ")+getFullAddress()+" try to get data_access.yml path");
                                    exit = true;

                                    sendDefaultRequest(printWriter, request);
                                }
                            }

                            default -> {
                                exit = true;

                                sendDefaultRequest(printWriter, request);
                            }
                        }

                    } catch(Exception error) {
                        break;
                    }
                }

                scheduleur.shutdownNow();

                if(exit) printWriter.println("closed");

                printWriter.close();
                clientSocket.close();
                bufferedReader.close();

                Lumy.logger.info("Connection closed "+getClientName()+": "+getFullAddress());
            } catch(IOException error) {
                Lumy.logger.log(Level.ERROR, error.getMessage(), error);
            }

        }

        private void stopThreadAction() {
            logger.warn("A "+delay+" seconds delay was exceeded with the client"+getClientName()+" : "+getFullAddress());
            exit = true;
            try {
                // End the line —→ final String message = bufferedReader.readLine();
                clientSocket.close();
            } catch(IOException error) {
                Lumy.logger.log(Level.ERROR, error.getMessage(), error);
            }
        }

        private String getClientName() {
            return (name.isEmpty() || name.isBlank()) ? "" : "["+name+"]";
        }

        private String getFullAddress() {
            return clientSocket.getInetAddress()+":"+clientSocket.getPort();
        }

        private boolean isLocalIP() {
            return clientSocket.getInetAddress().toString().equals("/127.0.0.1");
        }

        private void sendDefaultRequest(PrintWriter printWriter, String request) {
            printWriter.println("error: The request "+request+" doesn't exist");
        }

    }

    private void updateDataBase() {
        // 1000millisec = 1sec , so 1000*60millisec = 1min , so 1000*60*5millisec = 300000millisec = 5min
        if(System.currentTimeMillis()-lastUpdate < 300000L) return;
        lastUpdate = System.currentTimeMillis();

        LanguageManager.getLastLanguagesModifiedTime(3, TimeUnit.HOURS).stream().parallel().forEach(LanguageManager::updateTexteRedis);
    }

    private void updateDataBaseNow() {
        Arrays.stream(LanguageManager.values()).parallel().filter(language -> language != LanguageManager.DEFAULT).forEach(LanguageManager::updateTexteRedis);
    }

}