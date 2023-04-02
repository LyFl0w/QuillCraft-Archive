package net.lyflow.entitytest.entity;

import net.lyflow.entitytest.reader.SchematicReader;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Mask {

    private final World world;
    private final ArrayList<BlockDisplay> blockDisplayList;
    private final Vector vector;

    public Mask(Location location, SchematicReader schematicReader, Vector vector) {
        this.world = location.getWorld();
        this.blockDisplayList = new ArrayList<>();
        this.vector = vector;

        schematicReader.getBlocks().forEach((simpleLocation, blockData) ->{
            final BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(simpleLocation.mergeLocation(location), EntityType.BLOCK_DISPLAY);
            blockDisplay.setBlock(blockData);
            blockDisplayList.add(blockDisplay);
        });
    }

    /*
     * The code below allows the entities to be moved almost perfectly
     *
    public void applyVector() {
        final double[] distance = {vector.length()};
        final Vector direction = vector.normalize();
        final int ticksPerSecond = world.getGameRuleValue(GameRule.RANDOM_TICK_SPEED);
        final double speed = 2.0;
        final double distancePerTick = speed / ticksPerSecond;

        Bukkit.getScheduler().runTaskTimer(EntityTest.getInstance(), task -> {
            if (distance[0] <= 0) {
                task.cancel();
                return;
            }

            double moveDistance = Math.min(distance[0], distancePerTick);

            Vector moveVector = direction.clone().multiply(moveDistance);

            blockDisplayList.forEach(blockDisplay -> {
                Location newLocation = blockDisplay.getLocation().add(moveVector);

                blockDisplay.teleport(newLocation);
            });

            distance[0] -= moveDistance;
        }, 0L, 1L);
    }*/

    public void applyVector() {
        blockDisplayList.forEach(blockDisplay -> blockDisplay.setVelocity(vector));
    }

    public Vector getVelocity() {
        return vector;
    }


}
