package net.quillcraft.core.utils;

import org.bukkit.Location;

public class LocationUtils {

    public static Location roundCoordinates(Location location){
        return new Location(location.getWorld(), MathUtils.round(location.getX(), 2), MathUtils.round(location.getY(), 2),
                MathUtils.round(location.getZ(), 2), MathUtils.round(location.getYaw(), 2), MathUtils.round(location.getPitch(), 2));
    }

}
