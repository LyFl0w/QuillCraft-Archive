package net.lyflow.entitytest.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class SimpleLocation {

    private double x;
    private double y;
    private double z;

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

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    public Location getLocation(World world) {
        return new Location(world, this.x, this.y, this.z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public SimpleLocation addVector(@Nonnull Vector vector) {
        this.x += vector.getX();
        this.y += vector.getY();
        this.z += vector.getZ();

        return this;
    }

    public SimpleLocation substractVector(@Nonnull Vector vector) {
        this.x -= vector.getX();
        this.y -= vector.getY();
        this.z -= vector.getZ();

        return this;
    }
}
