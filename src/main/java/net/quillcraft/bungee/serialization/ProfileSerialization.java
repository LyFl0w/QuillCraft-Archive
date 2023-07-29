package net.quillcraft.bungee.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

public abstract class ProfileSerialization {

    protected final Gson gson;

    protected ProfileSerialization() {
        this.gson = new GsonBuilder().serializeNulls().create();
    }

    /**
     * @param object Object
     * @return object in json format.
     */
    public String serialize(@NonNull Object object) {
        return gson.toJson(object);
    }

    /**
     * @param json String
     * @return json information referenced in its object
     */
    @Nullable
    public abstract Object deserialize(@NonNull String json);
}
