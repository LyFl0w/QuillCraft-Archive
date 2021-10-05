package net.quillcraft.bungee.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.quillcraft.commons.account.Account;

import java.util.*;

public class ProfileSerializationManager {

    private final Gson gson;

    public ProfileSerializationManager(){
        this.gson = new GsonBuilder().serializeNulls().create();
    }

    public String serialize(Object objet){
        return gson.toJson(objet);
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
        final List<UUID> uuidList = new ArrayList<>();
        ((List<String>)gson.fromJson(json, List.class)).stream().parallel().forEach(uuidString -> {
             uuidList.add(UUID.fromString(uuidString));
         });
         return uuidList;
    }

    public List<String> deserializePartyMembersNames(String json){
        return (List<String>)gson.fromJson(json, List.class);
    }

}
