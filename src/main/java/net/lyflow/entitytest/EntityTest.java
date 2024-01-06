package net.lyflow.entitytest;

import net.lyflow.entitytest.command.CommandMide;
import net.lyflow.entitytest.command.CommandShip;
import net.lyflow.entitytest.entity.*;

import net.lyflow.entitytest.listener.entity.EntityCombustListener;
import net.lyflow.entitytest.listener.player.PlayerMoveListener;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;

public class EntityTest extends JavaPlugin {

    private final HashMap<String, Mask> maskHashMap = new HashMap<>();
    private static EntityTest INSTANCE;

    private final List<CustomVehicle> customVehicles = new ArrayList<>();
    
    @Override
    public void onEnable() {
        INSTANCE = this;

        Field frozenField = null;
        try {
            Field intrusiveHolderCache = MappedRegistry.class.getDeclaredField("m");
            intrusiveHolderCache.setAccessible(true);
            intrusiveHolderCache.set(BuiltInRegistries.ENTITY_TYPE, new IdentityHashMap<EntityType<?>, Holder.Reference<EntityType<?>>>());
            frozenField = MappedRegistry.class.getDeclaredField("l");
            frozenField.setAccessible(true);

            Registry.register(BuiltInRegistries.ENTITY_TYPE, "custommob", EntityType.Builder.of(CustomMob::new, MobCategory.CREATURE).fireImmune().build("custommob"));
            frozenField.set(BuiltInRegistries.ENTITY_TYPE, false);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

                /*Field frozenField;
        try {
            Field intrusiveHolderCache = DefaultedMappedRegistry.class.getDeclaredField("m");
            intrusiveHolderCache.setAccessible(true);
            intrusiveHolderCache.set(BuiltInRegistries.g, new IdentityHashMap<EntityTypes<?>, Holder.Reference<EntityType<?>>>());
            frozenField = DefaultedMappedRegistry.class.getDeclaredField("l");
            frozenField.setAccessible(true);

            Registry.register(BuiltInRegistries.g, "custommob", EntityTypes.Builder.a(CustomMob::new, MobCategory.CREATURE).fireImmune().build("custommob"));
            frozenField.set(BuiltInRegistries.g, false);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }*/

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityCombustListener(this), this);

        final CommandMide commandMide = new CommandMide(this);
        getCommand("mide").setExecutor(commandMide);
        getCommand("mide").setTabCompleter(commandMide);

        final CommandShip commandShip = new CommandShip(this);
        getCommand("ship").setExecutor(commandShip);
        getCommand("ship").setTabCompleter(commandShip);

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (CustomVehicle customVehicle : customVehicles) {
                if (customVehicle.getPlayer() != null)
                    customVehicle.rotate(customVehicle.getPlayer().getLocation().getYaw());
            }
        }, 0L, 1L);

    }

    public HashMap<String, Mask> getMaskHashMap() {
        return maskHashMap;
    }

    public List<CustomVehicle> getCustomVehicles() {
        return customVehicles;
    }

    public static EntityTest getInstance() {
        return INSTANCE;
    }
}
