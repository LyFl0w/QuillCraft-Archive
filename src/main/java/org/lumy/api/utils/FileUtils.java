package org.lumy.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class FileUtils{

    public static File getFileFromResource(File parent, String fileName){
        try{
            final File file = new File(parent, fileName);
            if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if(!file.exists()) Files.copy(Objects.requireNonNull(FileUtils.class.getClassLoader().getResourceAsStream(fileName)), file.toPath());

            System.out.println("return file");
            return file;
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("null exceptio");
        }
        System.out.println("noul");
        return null;
    }

    public static <T> T getObjectFromYamlFile(File file, Class<T> valueType) throws IOException{
        return new ObjectMapper(new YAMLFactory()).readValue(file, valueType);
    }



}
