package net.quillcraft.bungee.serialization;

import com.google.gson.reflect.TypeToken;
import net.quillcraft.commons.account.Account;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ProfileSerializationAccount {

    private ProfileSerializationAccount() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings("unchecked")
    public static class Particle extends ProfileSerialization {
        @Override
        public Map<Account.Particles, Boolean> deserialize(@Nonnull String json) {
            final Map<Account.Particles, Boolean> particles = new EnumMap<>(Account.Particles.class);
            ((HashMap<String, Boolean>) gson.fromJson(json, new TypeToken<HashMap<String, Boolean>>() {
            }.getType()))
                    .forEach((key, value) -> particles.put(Account.Particles.valueOf(key), value));

            return particles;
        }
    }
}