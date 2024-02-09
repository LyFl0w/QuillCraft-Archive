package net.quillcraft.lobby.npc;

import com.mojang.authlib.GameProfile;
import net.quillcraft.core.utils.LocationUtils;
import net.quillcraft.lobby.manager.ConfigurationBuilderManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NPCManager implements Listener {

    private static final ConfigurationBuilderManager configurationBuilderManager = ConfigurationBuilderManager.NPC;
    private final FileConfiguration fileConfiguration = configurationBuilderManager.getConfiguration();

    private final HashSet<NPC> npcList;
    private final JavaPlugin javaPlugin;

    private int distance;

    public NPCManager(JavaPlugin quillCraftLobby, int distance) {
        this.npcList = new HashSet<>();
        this.javaPlugin = quillCraftLobby;
        this.distance = (int) Math.pow(distance, 2);

        initNPC();

        quillCraftLobby.getServer().getPluginManager().registerEvents(this, quillCraftLobby);
    }

    private void initNPC() {
        fileConfiguration.getKeys(false).forEach(name -> fileConfiguration.getConfigurationSection(name).getKeys(false).forEach(reference -> {
            final String path = name+"."+reference;

            final List<String> skin = fileConfiguration.getStringList(path+".skin");
            final World world = Bukkit.getWorld(fileConfiguration.getString(path+".location.world"));
            final double x = fileConfiguration.getDouble(path+".location.x");
            final double y = fileConfiguration.getDouble(path+".location.y");
            final double z = fileConfiguration.getDouble(path+".location.z");

            final float yawHead = (float) fileConfiguration.getDouble(path+".location.yaw.head");
            final float yawBody = (float) fileConfiguration.getDouble(path+".location.yaw.body");
            final float pitch = (float) fileConfiguration.getDouble(path+".location.pitch");

            final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
            final NPC npc = new NPC(name, skin, Integer.parseInt(reference), new Location(world, x, y, z, yawBody, pitch), yawHead, gameProfile);

            this.npcList.add(npc);
            updateAllPlayersNPC(npc);
        }));
    }

    public NPC createNPC(String name, String skinName, Location location) {
        location = LocationUtils.roundCoordinates(location);
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
        final int reference = (int) npcList.stream().filter(npcs -> npcs.getName().equalsIgnoreCase(name)).count();
        final NPC npc = new NPC(name, skinName, reference, location, gameProfile);

        this.npcList.add(npc);

        final String path = name+"."+reference;
        final String[] skin = npc.getSkin(skinName);
        if(skin != null) fileConfiguration.set(path+".skin", skin);

        fileConfiguration.set(path+".location.world", location.getWorld().getName());
        fileConfiguration.set(path+".location.x", location.getX());
        fileConfiguration.set(path+".location.y", location.getY());
        fileConfiguration.set(path+".location.z", location.getZ());
        fileConfiguration.set(path+".location.yaw.head", location.getYaw());
        fileConfiguration.set(path+".location.yaw.body", location.getYaw());
        fileConfiguration.set(path+".location.pitch", location.getPitch());

        configurationBuilderManager.saveFile();

        updateAllPlayersNPC(npc);

        return npc;
    }

    public boolean exists(String npcName) {
        return this.npcList.stream().anyMatch(npc -> npc.getName().equals(npcName));
    }

    public boolean exists(String npcName, int reference) {
        return this.npcList.stream().anyMatch(npc -> npc.getName().equals(npcName) && npc.getReference() == reference);
    }

    public void removeNPC(String name, int reference) {
        final NPC npc = npcList.stream().parallel().filter(npcTarget -> npcTarget.getName().equalsIgnoreCase(name) && npcTarget.getReference() == reference).toList().get(0);
        npc.getWorld().getPlayers().forEach(npc::sendDespawnPacket);
        npc.getReceivers().clear();

        final StringBuilder pathBuilder = new StringBuilder(npc.getName());
        if(getNpcList().stream().parallel().filter(npcs -> npcs.getName().equalsIgnoreCase(name)).count() == 1)
            pathBuilder.append(".").append(npc.getReference());

        fileConfiguration.set(pathBuilder.toString(), null);

        configurationBuilderManager.saveFile();
        npcList.remove(npc);
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        displayNPC(event);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        displayNPC(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> this.npcList.stream().parallel().forEach(npc -> {
            npc.addReceiver(player);
            npc.sendSpawnPacket(player);
        }), 20L);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        npcList.stream().parallel().filter(npc -> npc.isReceiver(player)).forEach(npc -> {
            npc.removeReceiver(player);
            npc.sendDespawnPacket(player);
        });
    }

    public void setViewDistance(int distance) {
        this.distance = (int) Math.pow(distance, 2);
    }

    public Set<NPC> getNpcList() {
        return npcList;
    }

    private void displayNPC(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        npcList.stream().parallel().forEach(npc -> {
            final int action = this.canBeDisplayed(player, event.getFrom(), event.getTo(), npc);
            if(action == 1) {
                npc.sendSpawnPacket(player);
            } else if (action == 2) {
                npc.sendDespawnPacket(player);
            }
        });
    }

    private int canBeDisplayed(Player player, Location from, Location to, NPC npc) {
        if(!npc.isReceiver(player)) return 0;

        if(npc.getWorld() == to.getWorld()) {
            if(from.distanceSquared(npc.getLocation()) > this.distance && to.distanceSquared(npc.getLocation()) < this.distance) {
                return 1;
            } else if(from.distanceSquared(npc.getLocation()) < this.distance && to.distanceSquared(npc.getLocation()) > this.distance) {
                return 2;
            }
        }
        return 0;
    }

    public void onDisable() {
        npcList.forEach(npc -> npc.sendDespawnPacket(npc.getWorld().getPlayers()));
        npcList.clear();
    }

    private void updateAllPlayersNPC(NPC npc) {
        npc.getWorld().getPlayers().forEach(player -> {
            npc.addReceiver(player);
            npc.sendSpawnPacket(player);
        });
    }

}