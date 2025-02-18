package net.quillcraft.core.utils.builders;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryBuilder {

    private Inventory inventory;

    public InventoryBuilder(InventoryType inventoryType, String title) {
        inventory = Bukkit.createInventory(null, inventoryType, title);
    }

    public InventoryBuilder(InventoryType inventoryType) {
        this(inventoryType, "");
    }

    public InventoryBuilder(int size, String title) {
        inventory = Bukkit.createInventory(null, size, title);
    }

    public InventoryBuilder(int size) {
        this(size, null);
    }

    public InventoryBuilder(Inventory inventory) {
        this.inventory = inventory;
    }

    // FIXME : NOT REAL CLONE
    public InventoryBuilder(InventoryBuilder inventoryBuilder) {
        this(inventoryBuilder.inventory, "CLONE IMPLEMENT");
    }

    public InventoryBuilder(Inventory inventory, String title) {
        this.inventory = Bukkit.createInventory(null, inventory.getSize(), title);
        this.inventory.setContents(inventory.getContents());
    }

    public InventoryBuilder addItem(ItemStack item) {
        inventory.addItem(item);
        return this;
    }

    public InventoryBuilder setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
        return this;
    }

    public InventoryBuilder setTitle(String title) {
        inventory = new InventoryBuilder(inventory, title).toInventory();
        return this;
    }

    public InventoryBuilder setContents(ItemStack[] items) {
        inventory.setContents(items);
        return this;
    }

    public InventoryBuilder clear() {
        inventory.clear();
        return this;
    }

    public Inventory toInventory() {
        return inventory;
    }


}
