package org.lumy.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

public class FileConfiguration{

    private final HashMap<Object, Object> data;
    private final FileConfiguration.Section configurationSection;
    public FileConfiguration(InputStream inputStream){
        data = new Yaml().load(inputStream);
        configurationSection = new Section();
    }

    @SuppressWarnings("unchecked")
    public Object get(String path){
        final Iterator<String> pathPoint = configurationSection.parsePath(path);

        try{
            HashMap<String, Object> dataSearching = (HashMap<String, Object>) data.get(pathPoint.next());
            Object objectToFind = null;

            while(pathPoint.hasNext()){
                objectToFind = dataSearching.get(pathPoint.next());
                if(objectToFind instanceof HashMap<?, ?>){
                    dataSearching = (HashMap<String, Object>) objectToFind;
                }
            }

            return objectToFind;

        }catch(ClassCastException ignored){}
        return null;
    }

    public FileConfiguration getConfigurationSection(String path){
        configurationSection.setConfiguration(path.endsWith(".") ? path : path+".");
        return this;
    }

    @SuppressWarnings("unchecked")
    private <T> T getObject(Class<T> t, String path){
        final Object getObject = get(path);
        return (t.isInstance(getObject)) ? (T) getObject : null;
    }

    public String getString(String path){
        return getObject(String.class, path);
    }

    public Integer getInteger(String path){
        return getObject(Integer.class, path);
    }

    public Double getDouble(String path){
        return getObject(Double.class, path);
    }

    public Long getLong(String path){
        return getObject(Long.class, path);
    }

    public Float getFloat(String path){
        return getObject(Float.class, path);
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getStringList(String path){
        return getObject(Collections.<String>emptyList().getClass(), path);
    }

    private static class Section{

        private final StringBuilder sectionPathBuilder;

        private Section(){
            this.sectionPathBuilder = new StringBuilder();
        }

        private Section(String defaultPath){
            this.sectionPathBuilder = new StringBuilder(defaultPath);
        }

        private Iterator<String> parsePath(String tempPath){
            if(tempPath.isBlank()) return Arrays.stream(getPath().split("\\.")).iterator();
            return Arrays.stream((getPath()+((!tempPath.contains(".")) ? "."+tempPath : tempPath)).split("\\.")).iterator();
        }

        private void setConfiguration(String path){
            sectionPathBuilder.setLength(0);
            sectionPathBuilder.append(path);
        }

        private String getPath(){
            return sectionPathBuilder.toString();
        }

    }

}
