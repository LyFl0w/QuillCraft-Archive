package net.lyflow.entitytest.entity;

import net.lyflow.entitytest.EntityTest;
import net.lyflow.entitytest.reader.SchematicReader;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Mask {

    private final Player player;
    private final World world;
    private List<BlockDisplay> blockDisplayList;
    private final Vector translation;

    private boolean isFollow;
    private int taskId = -1;

    public Mask(Player owner, Location location, SchematicReader schematicReader, Vector translation) {
        this.player = owner;
        this.world = location.getWorld();
        this.blockDisplayList = new ArrayList<>();
        this.translation = translation;
        this.isFollow = false;

        schematicReader.getBlocks().forEach((simpleLocation, blockData) -> {
            final BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(simpleLocation.mergeLocation(location), EntityType.BLOCK_DISPLAY);
            blockDisplay.setBlock(blockData);
            blockDisplay.setInterpolationDuration(1);
            blockDisplayList.add(blockDisplay);
        });

    }

    public void applyVector() {
        applyVector(translation);
    }

    public boolean isFollow() {
        return isFollow;
    }

    public boolean toggleFollow() {
        this.isFollow = !this.isFollow;

        return this.isFollow;
    }


    public void applyVector(Vector vector) {
        //final List<BlockDisplay> blockDisplayList = new ArrayList<>();

        this.blockDisplayList.forEach(blockDisplay -> {
           blockDisplay.teleport(blockDisplay.getLocation().add(vector));
           //blockDisplay.remove();
        });

        //this.blockDisplayList = blockDisplayList;
    }

    public void applyLocation(Location location) {
        final List<BlockDisplay> blockDisplayList = new ArrayList<>();

        this.blockDisplayList.forEach(blockDisplay -> {
            blockDisplayList.add((BlockDisplay) blockDisplay.copy(location));
            blockDisplay.remove();
        });

        this.blockDisplayList = blockDisplayList;
    }
}
