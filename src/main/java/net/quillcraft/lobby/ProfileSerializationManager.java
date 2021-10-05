package net.quillcraft.lobby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.quillcraft.commons.account.Account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfileSerializationManager {

    private final Gson gson;

    public ProfileSerializationManager(){
        this.gson = new GsonBuilder().serializeNulls().create();
    }

    public String serialize(HashMap<Account.Particles, Boolean> hashMap){
        return gson.toJson(hashMap);
    }

    public String serialize(List<UUID> uuidList){
        return gson.toJson(uuidList);
    }

    public HashMap<Account.Particles, Boolean> deserializeParticle(String json){
        final HashMap<String, Boolean> particlesString = gson.fromJson(json, HashMap.class);
        final HashMap<Account.Particles, Boolean> particles = new HashMap<>();
        for(Map.Entry<String, Boolean> entryParticlesString : particlesString.entrySet()){
            particles.put(Account.Particles.valueOf(entryParticlesString.getKey()), entryParticlesString.getValue());
        }
        return particles;
    }

    public List<UUID> deserializePartyFollowers(String json){
        return (List<UUID>)gson.fromJson(json, List.class);
    }

}
