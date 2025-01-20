package net.lyflow.entitytest.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

public class CustomMob extends Mob {
    public CustomMob(EntityType entityType, Level level) {
        super(entityType,level);
        super.setCanPickUpLoot(false);
        super.setAggressive(false);
        super.setSpeed(0);
        super.setCustomNameVisible(true);
        super.setCustomName(Component.nullToEmpty("custom mob name"));
        super.setSilent(true);
        super.setInvulnerable(true);
        super.noPhysics = true;
        super.setPose(Pose.STANDING);
        super.setNoGravity(true);
    }
}
