package net.quillcraft.lumy.api;

import net.quillcraft.lumy.api.utils.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class LumyClient {

    private static final String NAME_PARSER = "name:";

    private final LinkedList<String> result;
    private final Logger logger;

    public LumyClient(String[] args, Logger logger, File dataFolder) {
        this.result = new LinkedList<>();
        this.logger = logger;
        try {
            final LumyConfiguration lumyConfiguration = FileUtils.getObjectFromYamlFile(
                    FileUtils.getFileFromResource(dataFolder, "config-lumy.yml"), LumyConfiguration.class);
            start(lumyConfiguration.name(), args, lumyConfiguration.ip(), lumyConfiguration.port());
        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    }

    private void start(String name, String[] actions, String ip, int port) {
        logger.info("Attempting to connect to the Lumy server");

        if (hasDuplicateName(actions)) {
            logger.warn("You can't have multiple names");
            return;
        }

        try (final Socket socket = new Socket(InetAddress.getByName(ip), port)) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            if (isValidName(name)) {
                printWriter.println(NAME_PARSER + name);
                logger.info( "Successful connection to Lumy server as {}", name);
            } else {
                logger.info("Successful connection to Lumy server");
            }

            processAction(actions, printWriter, bufferedReader);

            printWriter.close();
            bufferedReader.close();
        } catch (UnknownHostException e) {
            logger.log(Level.WARN, "Unknown Host", e);
        } catch (IOException e) {
            logger.log(Level.WARN, "Connexion Refused", e);
        }
    }

    public void processAction(String[] actions, PrintWriter printWriter, BufferedReader bufferedReader) throws IOException {
        for (final String action : actions) {
            printWriter.println(action.toLowerCase());

            logger.info("{} request sent to Lumy server", action);

            if (action.startsWith(NAME_PARSER)) continue;

            switch (action.toLowerCase()) {
                case "update" -> {
                    // do nothing
                    break;
                }

                default -> {
                    final String respond = bufferedReader.readLine();

                    if (respond.startsWith("error:")) {
                        logger.error(respond);
                        return;
                    }
                    if (respond.equals("closed")) return;

                    result.addLast(respond);
                }
            }
        }
    }

    private boolean hasDuplicateName(String[] actions) {
        return Arrays.stream(actions).filter(action -> action.startsWith(NAME_PARSER)).count() > 1;
    }

    private boolean isValidName(String name) {
        return name != null && !name.isBlank();
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

    private record LumyConfiguration(String name, String ip, int port) {
    }

}