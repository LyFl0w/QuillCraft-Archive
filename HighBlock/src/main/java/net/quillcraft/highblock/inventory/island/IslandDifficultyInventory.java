package net.quillcraft.highblock.inventory.island;

import net.quillcraft.highblock.island.IslandDifficulty;
import net.quillcraft.highblock.utils.builder.InventoryBuilder;
import net.quillcraft.highblock.utils.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class IslandDifficultyInventory {

    private IslandDifficultyInventory() {
        throw new IllegalStateException("Inventory class");
    }

    public static Inventory getInventory() {
        final InventoryBuilder inventoryBuilder = new InventoryBuilder(9, "§6Difficulté de l'île");

        final AtomicInteger slot = new AtomicInteger(1);
        Arrays.stream(IslandDifficulty.values()).forEach(islandDifficulty ->
                inventoryBuilder.setItem(slot.getAndAdd(2), new ItemBuilder(islandDifficulty.getMaterial()).setName("§r" + islandDifficulty.getName()).toItemStack()));
        inventoryBuilder.setItem(slot.get(), new ItemBuilder(Material.STRUCTURE_VOID).setName("§rAttendre une invitation").toItemStack());

        return inventoryBuilder.toInventory();
    }

}
