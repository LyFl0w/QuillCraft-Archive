package net.quillcraft.bungee.listeners.player;

import java.util.HashMap;
import java.util.UUID;

public record PlayerInfomation(String name, UUID uuid, byte rankID){

    public HashMap<String, Object> getHashMapInformation(){
        final HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("name", name);
        hashMap.put("uuid", uuid.toString());
        hashMap.put("rank", rankID);

        return hashMap;
    }

}
