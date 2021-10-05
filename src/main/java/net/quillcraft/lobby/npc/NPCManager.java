package net.quillcraft.lobby.npc;

import com.mojang.authlib.GameProfile;
import net.quillcraft.core.utils.LocationUtils;
import net.quillcraft.lobby.manager.ConfigurationManager;
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
import java.util.UUID;

public class NPCManager implements Listener {

    private final ConfigurationManager configurationManager = ConfigurationManager.NPC;
    private final FileConfiguration fileConfiguration = configurationManager.getConfiguration();

    private final HashSet<NPC> NPCList;
    private final JavaPlugin javaPlugin;

    private int distance;

    public NPCManager(JavaPlugin main, int distance){
        this.NPCList = new HashSet<>();
        this.javaPlugin = main;
        this.distance = (int) Math.pow(distance, 2);

        initNPC();

        main.getServer().getPluginManager().registerEvents(this, main);
    }

    private void initNPC(){
        fileConfiguration.getKeys(false).forEach(name -> fileConfiguration.getConfigurationSection(name).getKeys(false).forEach(reference -> {
            final String path = name+"."+reference;

            final List<String> skin = fileConfiguration.getStringList(path+".skin");
            final World world = Bukkit.getWorld(fileConfiguration.getString(path+".net.quillcraft.lobby.location.world"));
            final double x = fileConfiguration.getDouble(path+".net.quillcraft.lobby.location.x");
            final double y = fileConfiguration.getDouble(path+".net.quillcraft.lobby.location.y");
            final double z = fileConfiguration.getDouble(path+".net.quillcraft.lobby.location.z");

            final float yawHead = (float) fileConfiguration.getDouble(path+".net.quillcraft.lobby.location.yaw.head");
            final float yawBody = (float) fileConfiguration.getDouble(path+".net.quillcraft.lobby.location.yaw.body");
            final float pitch = (float) fileConfiguration.getDouble(path+".net.quillcraft.lobby.location.pitch");

            final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
            final NPC npc = new NPC(javaPlugin, name, skin, Integer.parseInt(reference), new Location(world, x, y, z, yawBody, pitch), yawHead, gameProfile);

            this.NPCList.add(npc);
            updateAllPlayersNPC(npc);
        }));
    }

    public NPC createNPC(String name, String skinName, Location location){
        location = LocationUtils.roundCoordinates(location);
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
        final int reference = (int) NPCList.stream().filter(npcs -> npcs.getName().equalsIgnoreCase(name)).count();
        final NPC npc = new NPC(javaPlugin, name, skinName, reference, location, gameProfile);

        this.NPCList.add(npc);

        final String path = name+"."+reference;
        final String[] skin = npc.getSkin(skinName);
        if(skin!=null) fileConfiguration.set(path+".skin", skin);

        fileConfiguration.set(path+".net.quillcraft.lobby.location.world", location.getWorld().getName());
        fileConfiguration.set(path+".net.quillcraft.lobby.location.x", location.getX());
        fileConfiguration.set(path+".net.quillcraft.lobby.location.y", location.getY());
        fileConfiguration.set(path+".net.quillcraft.lobby.location.z", location.getZ());
        fileConfiguration.set(path+".net.quillcraft.lobby.location.yaw.head", location.getYaw());
        fileConfiguration.set(path+".net.quillcraft.lobby.location.yaw.body", location.getYaw());
        fileConfiguration.set(path+".net.quillcraft.lobby.location.pitch", location.getPitch());

        configurationManager.saveFile();

        updateAllPlayersNPC(npc);

        return npc;
    }

    public boolean exists(String npcName){
        return this.NPCList.stream().anyMatch(npc -> npc.getName().equals(npcName));
    }

    public boolean exists(String npcName, int reference){
        return this.NPCList.stream().anyMatch(npc -> npc.getName().equals(npcName) && npc.getReference() == reference);
    }

    public void removeNPC(String name, int reference){
        final NPC npc = NPCList.stream().parallel().filter(npcTarget -> npcTarget.getName().equalsIgnoreCase(name) && npcTarget.getReference() == reference).toList().get(0);
        npc.getWorld().getPlayers().forEach(npc::sendDespawnPacket);
        npc.getReceivers().clear();

        final StringBuilder pathBuilder = new StringBuilder(npc.getName());
        if(getNPCList().stream().parallel().filter(npcs -> npcs.getName().equalsIgnoreCase(name)).count() == 1)
            pathBuilder.append(".").append(npc.getReference());

        fileConfiguration.set(pathBuilder.toString(), null);

        configurationManager.saveFile();
        NPCList.remove(npc);
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event){
        displayNPC(event);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event){
        displayNPC(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoin(PlayerJoinEvent event){
        final Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> this.NPCList.stream().parallel().forEach(npc -> {
            npc.addReceiver(player);
            npc.sendSpawnPacket(10, player);
        }), 20L);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event){
        final Player player = event.getPlayer();

        NPCList.stream().parallel().filter(npc -> npc.isReceiver(player)).forEach(npc ->{
            npc.removeReceiver(player);
            npc.sendDespawnPacket(player);
        });
    }

    public void setViewDistance(int distance){
        this.distance = (int) Math.pow(distance, 2);
    }

    public HashSet<NPC> getNPCList(){
        return NPCList;
    }

    private void displayNPC(PlayerMoveEvent event){
        final Player player = event.getPlayer();

        NPCList.stream().parallel().forEach(npc -> {
            switch(this.canBeDisplayed(player, event.getFrom(), event.getTo(), npc)){
                default -> {}
                case 1 -> npc.sendSpawnPacket(player);
                case 2 -> npc.sendDespawnPacket(player);
            }
        });
    }

    private int canBeDisplayed(Player player, Location from, Location to, NPC npc){
        if(!npc.isReceiver(player)) return 0;

        if(npc.getWorld() == to.getWorld()){
            if(from.distanceSquared(npc.getLocation()) > this.distance && to.distanceSquared(npc.getLocation()) < this.distance){
                return 1;
            }else if(from.distanceSquared(npc.getLocation()) < this.distance && to.distanceSquared(npc.getLocation()) > this.distance){
                return 2;
            }
        }
        return 0;
    }

    public void onDisable() {
        NPCList.forEach(npc -> npc.sendDespawnPacket(npc.getWorld().getPlayers()));
        NPCList.clear();
    }

    private void updateAllPlayersNPC(NPC npc){
        npc.getWorld().getPlayers().forEach(player -> {
            npc.addReceiver(player);
            npc.sendSpawnPacket(player);
        });
    }
}