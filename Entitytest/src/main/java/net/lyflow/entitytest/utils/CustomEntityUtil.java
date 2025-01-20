package net.lyflow.entitytest.utils;

import net.minecraft.world.entity.EntityTypes;

import java.util.Map;

public class CustomEntityUtil {

    @SuppressWarnings("unchecked")
    public static void register(String name, int id, Class<?> registryClass) {
        ((Map) ReflectionUtil.getPrivateField("c", EntityTypes.class, null)).put(name, registryClass);
        ((Map)ReflectionUtil.getPrivateField("d", EntityTypes.class, null)).put(registryClass, name);
        ((Map) ReflectionUtil.getPrivateField("f", EntityTypes.class, null)).put(registryClass, id);
    }

}
