package net.quillcraft.highblock.challenge.type;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.challenge.Challenge;
import net.quillcraft.highblock.challenge.Reward;
import net.quillcraft.highblock.event.itemshop.ShopEvent;
import net.quillcraft.highblock.shop.ItemShop;
import org.bukkit.Material;

import java.util.List;

public abstract class ShopChallenge<T extends ShopEvent> extends Challenge<T> {

    public ShopChallenge(HighBlock skyblock, int id, Difficulty difficulty, Type type, List<Integer> linkedChallengeID, List<Integer> counterList, List<List<ItemShop>> elementsCounter, Reward reward, int slot, Material material, String name, String... description) {
        super(skyblock, id, difficulty, type, linkedChallengeID, counterList, elementsCounter.stream().map(entityTypes -> entityTypes.stream().map(ItemShop::name).toList()).toList(), reward, slot, material, name, description);
    }

}
