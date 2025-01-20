package net.lyflow.entitytest.entity;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.EntityFlying;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityPhantom;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class Ship extends EntityPhantom  {

    public Ship(Location loc) {
        super(EntityTypes.av, ((CraftWorld) loc.getWorld()).getHandle());

        this.a_(loc.getX(), loc.getY(), loc.getZ());

        this.a(false); // Can Pick up Loot
        this.v(false); // Aggressive
        this.n(true); // Custom Name Visible
        this.b(IChatBaseComponent.a("Custom Entity")); // Custom Name

        ((CraftWorld) loc.getWorld()).getHandle().tryAddFreshEntityWithPassengers(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    // setSecondsOnFire & g -> Entity

    @Override
    public void g(int t) {
        // remove fire from sun
    }
}