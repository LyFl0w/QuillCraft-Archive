package net.quillcraft.lumy.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    public static File getFileFromResource(File parent, String fileName) {
        return getFileFromResource(new File(parent, fileName));
    }

    public static File getFileFromResource(String fileName) {
        try {
            final File file = new File(fileName);
            if (file.getParentFile() != null && !file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists())
                Files.copy(Objects.requireNonNull(FileUtils.class.getClassLoader().getResourceAsStream(fileName)), file.toPath());

            return file;
        } catch (Exception exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return null;
    }

    private static File getFileFromResource(File file) {
        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists())
                Files.copy(Objects.requireNonNull(FileUtils.class.getClassLoader().getResourceAsStream(file.getName())), file.toPath());

            return file;
        } catch (Exception exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return null;
    }


    public static <T> T getObjectFromYamlFile(File file, Class<T> valueType) throws IOException {
        return new ObjectMapper(new YAMLFactory()).readValue(file, valueType);
    }

}
