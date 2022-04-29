package net.quillcraft.parkourpvp.serialization;

import net.quillcraft.core.serialization.ProfileSerialization;
import net.quillcraft.parkourpvp.game.player.PlayerData;

import javax.annotation.Nonnull;

public class ProfileSerializationPlayerData extends ProfileSerialization{

    @Override
    public PlayerData deserialize(@Nonnull String json){
        return gson.fromJson(json, PlayerData.class);
    }

}