package net.quillcraft.bungee.utils;

import net.quillcraft.bungee.QuillCraftBungee;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class MessagesPropertiesUtils {

    public void generateNewBundleMessagesProperties(boolean generatedWithSpaces) {
        ResourceBundle baseBundle;
        try {
            baseBundle = ResourceBundle.getBundle("messages");
        } catch(MissingResourceException ex) {
            baseBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
        }

        try {
            final File file = new File("messages.properties");
            if(!file.isFile()) {
                file.createNewFile();

                StringBuilder messages = new StringBuilder();
                for(String keys : baseBundle.keySet()) {
                    messages.append(keys).append(":").append(baseBundle.getString(keys)).append("\n");
                    if(generatedWithSpaces) messages.append("\n");
                }
                Files.write(file.toPath(), messages.toString().getBytes());
                System.out.println("The messages.properties file has been successfully written !");
            }
            new PropertyResourceBundle(new FileReader(file));

        } catch(IOException exception) {
            QuillCraftBungee.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

}