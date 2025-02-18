package net.quillcraft.lumy.utils;

import net.quillcraft.lumy.Lumy;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;


public class FileConfiguration {

    private final HashMap<Object, Object> data;
    private final FileConfiguration.Section configurationSection;

    public FileConfiguration(InputStream inputStream) {
        data = new Yaml().load(inputStream);
        configurationSection = new Section();
    }

    @SuppressWarnings("unchecked")
    public Object get(String path) {
        try {
            if (configurationSection.pathIsEmpty() && !path.contains(".")) return data.get(path);

            final Iterator<String> pathPoint = configurationSection.parsePath(path);
            HashMap<String, Object> dataSearching = (HashMap<String, Object>) data.get(pathPoint.next());
            Object objectToFind = null;

            while (pathPoint.hasNext()) {
                objectToFind = dataSearching.get(pathPoint.next());
                if (objectToFind instanceof HashMap<?, ?>) {
                    dataSearching = (HashMap<String, Object>) objectToFind;
                }
            }

            return objectToFind;

        } catch (NullPointerException exception) {
            Lumy.logger.error("text with path : {} was not found !", path);
        } catch (ClassCastException exception) {
            Lumy.logger.error("text with path : {} was found, but the error is on the casting !", path);
        }
        return null;
    }

    public FileConfiguration getConfigurationSection(String path) {
        configurationSection.setConfiguration(path.endsWith(".") ? path : path + ".");
        return this;
    }

    @SuppressWarnings("unchecked")
    private <T> T getObject(Class<T> t, String path) {
        final Object getObject = get(path);
        return (t.isInstance(getObject)) ? (T) getObject : null;
    }

    public String getString(String path) {
        return getObject(String.class, path);
    }

    public Integer getInteger(String path) {
        return getObject(Integer.class, path);
    }

    public Double getDouble(String path) {
        return getObject(Double.class, path);
    }

    public Long getLong(String path) {
        return getObject(Long.class, path);
    }

    public Float getFloat(String path) {
        return getObject(Float.class, path);
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getStringList(String path) {
        return getObject(List.class, path);
    }

    private static class Section {

        private final StringBuilder sectionPathBuilder;

        private Section() {
            this.sectionPathBuilder = new StringBuilder();
        }

        private Iterator<String> parsePath(String tempPath) {
            final String regex = "\\.";
            if (tempPath.isBlank()) return Arrays.stream(getPath().split(regex)).iterator();
            if (pathIsEmpty()) return Arrays.stream(tempPath.split(regex)).iterator();
            return Arrays.stream((getPath() + ((!tempPath.contains(".")) ? "." + tempPath : tempPath)).split(regex)).iterator();
        }

        private void setConfiguration(String path) {
            sectionPathBuilder.setLength(0);
            sectionPathBuilder.append(path);
        }

        private boolean pathIsEmpty() {
            return sectionPathBuilder.isEmpty();
        }

        private String getPath() {
            return sectionPathBuilder.toString();
        }

    }

}
