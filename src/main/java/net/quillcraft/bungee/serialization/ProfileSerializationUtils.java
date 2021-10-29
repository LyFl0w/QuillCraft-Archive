package net.quillcraft.bungee.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileSerializationUtils {

    public static class ListString extends ProfileSerialization{
        @Override
        public List<String> deserialize(String json){
            return gson.fromJson(json, List.class);
        }
    }

    public static class ListUUID extends ProfileSerialization{
        @Override
        public List<UUID> deserialize(String json){
            final List<UUID> uuidList = new ArrayList<>();
            gson.fromJson(json, List.class).
                    forEach(uuidString -> uuidList.add(UUID.fromString((String) uuidString)));
            return uuidList;
        }
    }

}
