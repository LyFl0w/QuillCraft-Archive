package net.lyflow.entitytest.listener.entity;

import net.lyflow.entitytest.EntityTest;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;

public class EntityCombustListener implements Listener {

    private final EntityTest entityTest;
    public EntityCombustListener(EntityTest entityTest) {
        this.entityTest = entityTest;
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity().getType() == EntityType.PHANTOM) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityCombustB(EntityCombustByEntityEvent event) {
        System.out.println(event.getEntity().getCustomName()+" / "+event.getEntity().getName()+" b");
    }

}
