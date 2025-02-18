package net.quillcraft.highblock.manager;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.challenge.Challenge;
import net.quillcraft.highblock.challenge.Reward;
import net.quillcraft.highblock.challenge.mod.CraftItemChallenge;
import net.quillcraft.highblock.challenge.mod.block.PlaceBlockChallenge;
import net.quillcraft.highblock.challenge.mod.block.RemoveBlockChallenge;
import net.quillcraft.highblock.challenge.mod.entity.KillEntityChallenge;
import net.quillcraft.highblock.challenge.mod.entity.ReproduceAnimalChallenge;
import net.quillcraft.highblock.challenge.mod.shop.BuyItemChallenge;
import net.quillcraft.highblock.challenge.mod.shop.SellItemChallenge;
import net.quillcraft.highblock.shop.ItemShop;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChallengeManager {

    private final List<Challenge<? extends Event>> registeredChallenges = new ArrayList<>();

    public ChallengeManager(HighBlock highblock) {
        createChallenges(highblock);
        registerChallengesEvent(highblock, highblock.getServer().getPluginManager());
    }

    public void init() {
        registeredChallenges.stream().parallel().forEach(challenge -> challenge.getChallengeProgress().updateDefaultChallengeStatus());
    }


    private void createChallenges(HighBlock highblock) {
        addNewChallenges(
                new RemoveBlockChallenge(highblock, 0, Challenge.Difficulty.EASY, List.of(5), List.of(List.of(Material.OAK_LOG)),
                        new Reward(List.of(new ItemStack(Material.BREAD, 5))), 1, Material.OAK_LOG, "La voie du bucheron n°1", "Le bois est un element essentiel pour votre surive"),

                new RemoveBlockChallenge(highblock, 1, Challenge.Difficulty.EASY, List.of(6), List.of(List.of(Material.COBBLESTONE)),
                        new Reward(List.of(new ItemStack(Material.BREAD, 5))), 2, Material.COBBLESTONE, "La voie du mineur n°1", "La pierre tout comme le bois est un element essentiel"),

                new CraftItemChallenge(highblock, 2, Challenge.Difficulty.EASY, List.of(1, 1), Arrays.asList(List.of(Material.STONE_AXE), List.of(Material.STONE_PICKAXE)),
                        new Reward(), 3, Material.SCULK_SENSOR, "Started from the bottom"),

                new SellItemChallenge(highblock, 3, Challenge.Difficulty.EASY, List.of(64), List.of(List.of(ItemShop.COBBLESTONE)),
                        new Reward(List.of(new ItemStack(Material.COW_SPAWN_EGG, 2)), 2, 0), 4, Material.SALMON, "Still at the bottom n°1"),

                new SellItemChallenge(highblock, 4, Challenge.Difficulty.EASY, List.of(64), List.of(List.of(ItemShop.OAK_LOG)),
                        new Reward(List.of(new ItemStack(Material.SHEEP_SPAWN_EGG, 2)), 2, 0), 5, Material.COOKED_COD, "Still at the bottom n°2")
        );
    }

    private void registerChallengesEvent(HighBlock highblock, PluginManager pluginManager) {
        pluginManager.registerEvents(new ReproduceAnimalChallenge.ListenerEvent(this), highblock);
        pluginManager.registerEvents(new KillEntityChallenge.ListenerEvent(this), highblock);

        pluginManager.registerEvents(new BuyItemChallenge.ListenerEvent(this), highblock);
        pluginManager.registerEvents(new SellItemChallenge.ListenerEvent(this), highblock);

        pluginManager.registerEvents(new PlaceBlockChallenge.ListenerEvent(this), highblock);
        pluginManager.registerEvents(new RemoveBlockChallenge.ListenerEvent(this), highblock);

        pluginManager.registerEvents(new CraftItemChallenge.ListenerEvent(this), highblock);
    }

    public List<Challenge<? extends Event>> getRegisteredChallenges() {
        return registeredChallenges;
    }

    public boolean challengeExist(int id) {
        return registeredChallenges.stream().parallel().anyMatch(challenge -> challenge.getID() == id);
    }

    public boolean challengeExist(String name) {
        return registeredChallenges.stream().parallel().anyMatch(challenge -> challenge.getName().equalsIgnoreCase(name));
    }

    @Nullable
    public Challenge<? extends Event> getChallengeByID(int id) {
        return registeredChallenges.stream().parallel().filter(challenge -> challenge.getID() == id).findFirst().orElse(null);
    }

    @Nullable
    public Challenge<? extends Event> getChallengeByName(String name) {
        return registeredChallenges.stream().parallel().filter(challenge -> challenge.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Nullable
    public List<? extends Challenge<? extends Event>> getChallengesByType(Challenge.Type type) {
        return registeredChallenges.stream().parallel().filter(challenge -> challenge.getType() == type).toList();
    }

    @Nullable
    public List<? extends Challenge<? extends Event>> getChallengesByDifficulty(Challenge.Difficulty difficulty) {
        return registeredChallenges.stream().parallel().filter(challenge -> challenge.getDifficulty() == difficulty).toList();
    }

    @SafeVarargs
    public final void addNewChallenges(Challenge<? extends Event>... challenges) {
        Arrays.stream(challenges).forEach(challenge -> {
            final String name = challenge.getName();
            final int id = challenge.getID();

            if (challengeExist(id)) {
                final Challenge<?> otherChallenge = getChallengeByID(id);
                throw new IllegalArgumentException((challenge.equals(otherChallenge)) ? "Their is a duplication of Challenge with id " + id : "The Challenge " + challenge.getName() + " can't be initialized because his id is already use by the Challenge " + otherChallenge.getName());
            } else if (challengeExist(name)) {
                final Challenge<?> otherChallenge = getChallengeByName(name);
                throw new IllegalArgumentException((challenge.equals(otherChallenge)) ? "Their is a duplication of Challenge with name " + name : "The Challenge " + challenge.getName() + " can't be initialized because his name is already use by this Challenge ID " + otherChallenge.getID());
            }

            getRegisteredChallenges().add(challenge);
        });
    }

}
