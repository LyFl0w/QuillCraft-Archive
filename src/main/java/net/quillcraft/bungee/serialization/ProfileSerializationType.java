package net.quillcraft.bungee.serialization;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;

public class ProfileSerializationType {

    private final Gson gson;

    public ProfileSerializationType() {
        this(new GsonBuilder().serializeNulls());
    }

    public ProfileSerializationType(GsonBuilder gsonBuilder) {
        this.gson = gsonBuilder.create();
    }

    /**
     * @param object Object
     * @return object in json format.
     */
    public String serialize(Object object) {
        return gson.toJson(object);
    }

    /**
     * @param json String
     * @return json information referenced in its object
     */
    public <T> T deserialize(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public <T> T deserialize(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }

    public <T> List<T> deserializeList(String json, Class<T> classOfListT) {
        return gson.fromJson(json, new TypeToken<List<T>>() {}.getType());
    }

    public <K, V> HashMap<K, V> deserializeHashMap(String json, Class<K> classOfKeyT, Class<V> classOfValueT) {
        return gson.fromJson(json, new TypeToken<HashMap<K, V>>() {}.getType());
    }


}
