package net.quillcraft.core.serialization;

import com.google.gson.reflect.TypeToken;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileSerializationUtils {

    private ProfileSerializationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static class ListString extends ProfileSerialization {
        @Override
        public List<String> deserialize(@Nonnull String json) {
            return gson.fromJson(json, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
    }

    @SuppressWarnings("unchecked")
    public static class ListUUID extends ProfileSerialization {
        @Override
        public List<UUID> deserialize(@Nonnull String json) {
            final List<UUID> uuidList = new ArrayList<>();
            ((List<String>) gson.fromJson(json, new TypeToken<ArrayList<String>>() {
            }.getType())).forEach(uuidString -> uuidList.add(UUID.fromString(uuidString)));
            return uuidList;
        }
    }

}
