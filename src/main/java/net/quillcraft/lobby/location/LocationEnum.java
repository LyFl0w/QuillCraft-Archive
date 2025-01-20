package net.quillcraft.lobby.location;

import net.quillcraft.lobby.QuillCraftLobby;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public enum LocationEnum {

    LOBBY_SPAWN(new Location(Bukkit.getWorlds().get(0),
            getFileConfiguration().getDouble("spawn.x"),
            getFileConfiguration().getDouble("spawn.y"),
            getFileConfiguration().getDouble("spawn.z"),
            (float) getFileConfiguration().getDouble("spawn.yaw"),
            (float) getFileConfiguration().getDouble("spawn.pitch")));

    private Location location;

    LocationEnum(Location location) {
        this.location = location;
    }

    private static FileConfiguration getFileConfiguration() {
        return QuillCraftLobby.getInstance().getConfig();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;

        final FileConfiguration fileConfiguration = getFileConfiguration();
        fileConfiguration.set("spawn.x", location.getBlockX());
        fileConfiguration.set("spawn.y", location.getBlockY());
        fileConfiguration.set("spawn.z", location.getBlockZ());
        fileConfiguration.set("spawn.yaw", location.getYaw());
        fileConfiguration.set("spawn.pitch", location.getPitch());

        QuillCraftLobby.getInstance().saveConfig();
    }
}
