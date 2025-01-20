package net.lyflow.entitytest.entity;

import net.lyflow.entitytest.EntityTest;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomVehicle {

    private static final Vector3f DEFAULT_SIZE = new Vector3f(1);
    private static final AxisAngle4f DEFAULT_ANGLE = new AxisAngle4f();

    private final Player player;
    private final World world;
    private final List<BlockDisplay> blockDisplayList;
    private final List<Location> locations;

    private float yaw;

    public CustomVehicle(Player owner, SchematicReader schematicReader) {
        this.player = owner;
        this.world = player.getWorld();
        this.blockDisplayList = new ArrayList<>();
        this.locations = new ArrayList<>();

        this.yaw = 0;

        schematicReader.getBlocks().forEach((simpleLocation, blockData) -> {
            final BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(player.getLocation(), EntityType.BLOCK_DISPLAY);

            blockDisplay.setInterpolationDuration(1);

            blockDisplay.setTransformation(new Transformation(
                    simpleLocation.toVector().toVector3f(),
                    DEFAULT_ANGLE,
                    DEFAULT_SIZE,
                    DEFAULT_ANGLE));

            player.addPassenger(blockDisplay);
            blockDisplay.setBlock(blockData);

            blockDisplayList.add(blockDisplay);
            locations.add(simpleLocation.getLocation(world));
        });
    }

    public void kill() {
        EntityTest.getInstance().getCustomVehicles().remove(this);
        blockDisplayList.forEach(Entity::remove);
    }

    public static void addCustomVehicle(Player player, String name, EntityTest entityTest) throws IOException {
        final CustomVehicle customVehicle = new CustomVehicle(player,
                new SchematicReader(new FileInputStream(new File(entityTest.getDataFolder(), name)), new Vector(3.5, 5.8, 7.8)));

        customVehicle.rotate(player.getLocation().getYaw());

        entityTest.getCustomVehicles().add(customVehicle);

    }

    @Nullable
    public static CustomVehicle getCustomVehicle(Player player,  EntityTest entityTest) {
        for(CustomVehicle customVehicle : entityTest.getCustomVehicles()) {
            if(customVehicle.player.getName().equals(player.getName())) {
                return customVehicle;
            }
        }
        return null;
    }

    public static void removeCustomVehicle(Player player, EntityTest entityTest) {
        for(CustomVehicle customVehicle : entityTest.getCustomVehicles()) {
            if(customVehicle.player.getName().equals(player.getName())) {
                customVehicle.kill();
                return;
            }
        }
        System.out.println("CustomVehicle doesn't exist !");
    }

    public void rotate(float yaw) {
        final boolean rotateYaw = Math.abs(yaw - this.yaw) >= 5;

        if(!rotateYaw) return;

        this.yaw = yaw;
        rotateYaw();
    }

    private void rotateYaw() {
        final double radians = Math.toRadians(this.yaw);
        final double cos = Math.cos(radians);
        final double sin = Math.sin(radians);

        final AxisAngle4f angle4f = new AxisAngle4f((float) (yaw * Math.PI / -180), 0, 1, 0);

        for (int i = 0; i < blockDisplayList.size(); i++) {
            final Location location = locations.get(i);
            final BlockDisplay blockDisplay = blockDisplayList.get(i);

            // translate to origin
            final double x0 = location.getX();
            final double z0 = location.getZ();

            // rotate
            final double newX = x0 * cos - z0 * sin;
            final double newZ = x0 * sin + z0 * cos;

            blockDisplay.setInterpolationDelay(0);
            blockDisplay.setTransformation(new Transformation(
                    new Vector(newX, location.getY(), newZ).toVector3f(),
                    angle4f,
                    DEFAULT_SIZE,
                    DEFAULT_ANGLE));
        }
    }

    public Player getPlayer() {
        return player;
    }

    /*

    @Deprecated
    private void rotatePitch() {
        System.out.println("pitch");
        final double radians = Math.toRadians(this.pitch);
        final double cos = Math.cos(radians);
        final double sin = Math.sin(radians);

        this.angleRight = new AxisAngle4f((float) (this.pitch * Math.PI / 180), 1, 0, 0);

        for (int i = 0; i < blockDisplayList.size(); i++) {
            final Location location = locations.get(i);
            final BlockDisplay blockDisplay = blockDisplayList.get(i);

            // translate to origin
            final double y0 = location.getY();
            final double z0 = location.getZ();

            // rotate
            final double newY = y0 * cos - z0 * sin;
            final double newZ = y0 * sin + z0 * cos;

            blockDisplay.setInterpolationDelay(0);
            blockDisplay.setTransformation(new Transformation(
                    new Vector(location.getX(), newY, newZ).toVector3f(),
                    angleLeft,
                    new Vector3f(1),
                    angleRight));
        }
    }

    @Deprecated
    private void rotateYawPitch() {
        System.out.println("yaw & pitch");
        final double radiansYaw = Math.toRadians(this.yaw);
        final double radiansPitch = Math.toRadians(this.pitch);
        final double cosYaw = Math.cos(radiansYaw);
        final double sinYaw = Math.sin(radiansYaw);
        final double cosPitch = Math.cos(radiansPitch);
        final double sinPitch = Math.sin(radiansPitch);

        this.angleLeft = new AxisAngle4f((float) (this.yaw * Math.PI / -180), 0, 1, 0);
        this.angleRight = new AxisAngle4f((float) (this.pitch * Math.PI / 180), 1, 0, 0);

        for (int i = 0; i < blockDisplayList.size(); i++) {
            final Location location = locations.get(i);
            final BlockDisplay blockDisplay = blockDisplayList.get(i);

            final double x0 = location.getX();
            final double y0 = location.getY();
            final double z0 = location.getZ();

            // Rotation Yaw
            final double tempX = x0 * cosYaw - z0 * sinYaw;
            final double tempZ = x0 * sinYaw + z0 * cosYaw;

            // Rotation Pitch (après Yaw)
            final double newX = tempX;
            final double newY = y0 * cosPitch + tempZ * sinPitch;
            final double newZ = -y0 * sinPitch + tempZ * cosPitch;

            blockDisplay.setInterpolationDelay(0);
            blockDisplay.setTransformation(
                    new Transformation(
                            new Vector(newX, newY, newZ).toVector3f(),
                            angleLeft,
                            new Vector3f(1),
                            angleRight));
        }
    }

     */

}
