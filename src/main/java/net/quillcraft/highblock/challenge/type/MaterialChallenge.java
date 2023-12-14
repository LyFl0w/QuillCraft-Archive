package net.quillcraft.highblock.challenge.type;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.challenge.Challenge;
import net.quillcraft.highblock.challenge.Reward;

import org.bukkit.Material;
import org.bukkit.event.Event;

import java.util.List;

public abstract class MaterialChallenge<T extends Event> extends Challenge<T> {

    public MaterialChallenge(HighBlock skyblock, int id, Difficulty difficulty, Type type, List<Integer> linkedChallengeID, List<Integer> counterList, List<List<Material>> elementsCounter, Reward reward, int slot, Material material, String name, String... description) {
        super(skyblock, id, difficulty, type, linkedChallengeID, counterList, elementsCounter.stream().map(entityTypes -> entityTypes.stream().map(Material::name).toList()).toList(), reward, slot, material, name, description);
    }

}
