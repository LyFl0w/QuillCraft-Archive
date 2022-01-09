package org.lumy.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FileUtils{

    public static File getFileFromResource(String fileName){
        try{
            final File file = new File(System.getProperty("user.dir"), fileName);
            if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if(!file.exists()) Files.copy(getFileInputStreamFromResource(fileName), file.toPath());

            return file;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getFileInputStreamFromResource(String fileName){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }

    public static <T> T getObjectFromYamlFile(File file, Class<T> valueType) throws IOException{
        return new ObjectMapper(new YAMLFactory()).readValue(file, valueType);
    }



}
