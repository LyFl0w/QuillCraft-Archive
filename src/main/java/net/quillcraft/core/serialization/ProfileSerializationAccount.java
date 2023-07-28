package net.quillcraft.core.serialization;

import com.google.gson.reflect.TypeToken;
import net.quillcraft.commons.account.Account;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class ProfileSerializationAccount {

    @SuppressWarnings("unchecked")
    public static class Particle extends ProfileSerialization {
        @Override
        public HashMap<Account.Particles, Boolean> deserialize(@Nonnull String json) {
            final HashMap<Account.Particles, Boolean> particles = new HashMap<>();
            ((HashMap<String, Boolean>) gson.fromJson(json, new TypeToken<HashMap<String, Boolean>>() {}.getType())).forEach((key, value) -> particles.put(Account.Particles.valueOf(key), value));

            return particles;
        }
    }
}