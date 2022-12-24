package net.lyflow.skyblock.challenge.type;

import net.lyflow.skyblock.SkyBlock;
import net.lyflow.skyblock.challenge.Challenge;
import net.lyflow.skyblock.challenge.Reward;

import org.bukkit.Material;
import org.bukkit.event.Event;

import java.util.List;

public abstract class MaterialChallenge<T extends Event> extends Challenge<T, Material> {

    public MaterialChallenge(SkyBlock skyBlock, int id, Difficulty difficulty, Type type, List<Integer> counterList, List<List<Material>> elementsCounter, Reward reward, Material material, String name, String... description) {
        super(skyBlock, id, difficulty, type, counterList, elementsCounter, reward, material, name, description);
    }

}
