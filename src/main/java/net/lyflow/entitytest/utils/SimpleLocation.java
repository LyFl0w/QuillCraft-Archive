package net.lyflow.entitytest.utils;

import org.bukkit.Location;
import org.bukkit.World;

public class SimpleLocation {

    private final double x, y, z;

    public SimpleLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SimpleLocation() {
        this(0, 0, 0);
    }

    public Location mergeLocation(Location location) {
        return location.clone().add(x, y, z);
    }

    public Location getLocation(World world) {
        return new Location(world, this.x, this.y, this.z);
    }

}
