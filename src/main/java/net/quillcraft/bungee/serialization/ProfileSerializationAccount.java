package net.quillcraft.bungee.serialization;

import net.quillcraft.commons.account.Account;

import java.util.HashMap;

public class ProfileSerializationAccount {

    public static class Particle extends ProfileSerialization {
        @Override
        public HashMap<Account.Particles, Boolean> deserialize(String json) {
            final HashMap<String, Boolean> particlesString = gson.fromJson(json, HashMap.class);
            final HashMap<Account.Particles, Boolean> particles = new HashMap<>();
            particlesString.forEach((key, value) -> particles.put(Account.Particles.valueOf(key), value));

            return particles;
        }
    }
}