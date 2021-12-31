package net.quillcraft.lobby.npc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.quillcraft.core.utils.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class NPC {

    private final Set<Player> receivers;

    private final JavaPlugin javaPlugin;

    private final EntityPlayer npc;
    private World world;
    private Location location;
    private final String name;
    private final int reference;
    private DataWatcher dataWatcher;
    private float yawHead;

    private NPC(JavaPlugin javaPlugin, String name, int reference, Location location, float yawHead, GameProfile gameProfile){
        this.receivers = new HashSet<>();
        this.javaPlugin = javaPlugin;
        this.name = name;
        this.reference = reference;
        this.yawHead = yawHead;

        final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        final WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        this.npc = new EntityPlayer(server, world, gameProfile);

        this.npc.getBukkitEntity().setPlayerListName("§8[NPC] §f"+npc.getName());
    }

    protected NPC(JavaPlugin javaPlugin, String name, List<String> skinPart, int reference, Location location, float yawHead, GameProfile gameProfile){
        this(javaPlugin, name, reference, location, yawHead, gameProfile);

        if(skinPart != null && skinPart.size() == 2){
            gameProfile.getProperties().put("textures", new Property("textures", skinPart.get(0), skinPart.get(1)));

            //DISPLAY 3D PART OF THE SKIN
            setDataWatcher();
        }

        setLocation(location);
        updateBodyRotation();
    }

    protected NPC(JavaPlugin javaPlugin, String name, String skinName, int reference, Location location, GameProfile gameProfile){
        this(javaPlugin, name, reference, location, location.getYaw(), gameProfile);

        if(!skinName.isBlank()){
            final String[] skinsType = getSkin(skinName);
            if(skinsType != null){
                gameProfile.getProperties().put("textures", new Property("textures", skinsType[0], skinsType[1]));

                //DISPLAY 3D PART OF THE SKIN
                setDataWatcher();
            }
        }
        setLocation(location);
        updateBodyRotation();
    }

    private void setDataWatcher(){
        dataWatcher = npc.getDataWatcher();
        dataWatcher.set(new DataWatcherObject<>(17, DataWatcherRegistry.a), (byte) 0xFF);
    }

    public NPC setLocation(Location location){
        this.location = location;
        this.world = location.getWorld();

        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return this;
    }

    public NPC addReceiver(List<Player> players){
        this.receivers.addAll(players);
        return this;
    }

    public NPC addReceiver(Player player){
        this.receivers.add(player);
        return this;
    }

    public NPC removeReceiver(List<Player> players){
        players.forEach(this.receivers::remove);
        return this;
    }

    public NPC removeReceiver(Player player){
        this.receivers.remove(player);
        return this;
    }

    public boolean isReceiver(Player player){
        return this.receivers.contains(player);
    }

    private ArrayList<Packet<?>> getSpawnPackets(){
        final ArrayList<Packet<?>> packets = new ArrayList<>();
        packets.add(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc)); //Display NPC
        packets.add(new PacketPlayOutNamedEntitySpawn(npc)); // Display NPC
        if(dataWatcher != null) packets.add(new PacketPlayOutEntityMetadata(getId(), dataWatcher, true)); // Display 3D part of the Skin
        packets.add(new PacketPlayOutAnimation(npc, 0)); // Rotate Correctly the body of NPC with left-click
        packets.add(new PacketPlayOutEntityHeadRotation(npc, (byte) (yawHead*256/360))); // Rotate head
        return packets;
    }

    private ArrayList<Packet<?>> getFullLocationPacket(){
        final ArrayList<Packet<?>> packets = new ArrayList<>();
        packets.add(getLocationPacket());
        packets.add(getHeadPacket());
        return packets;
    }

    private PacketPlayOutEntityHeadRotation getHeadPacket(){
        return new PacketPlayOutEntityHeadRotation(npc, (byte) (yawHead*256/360));
    }

    private PacketPlayOutEntityTeleport getLocationPacket(){
        return new PacketPlayOutEntityTeleport(npc);
    }

    private ArrayList<Packet<?>> getDestroyPackets(){
        final ArrayList<Packet<?>> packets = new ArrayList<>();
        packets.add(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc));
        packets.add(new PacketPlayOutEntityDestroy(getId()));

        return packets;
    }

    public void sendSpawnPacket(Player... players){
        sendSpawnPacket(2, players);
    }

    public void sendSpawnPacket(int time, Player... players){
        final List<Packet<?>> packets = getSpawnPackets();

        Arrays.stream(players).forEach(player -> {
            PacketUtils.sendPacket(player, packets);
            Bukkit.getScheduler().runTaskLaterAsynchronously(javaPlugin, () ->
                    PacketUtils.sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc)), time*20L);
        });
    }

    public void sendSpawnPacket(List<Player> players){
        final List<Packet<?>> packets = getSpawnPackets();
        players.forEach(player -> {
            PacketUtils.sendPacket(player, packets);
            Bukkit.getScheduler().runTaskLater(javaPlugin, () ->
                    PacketUtils.sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc)), 30L);
        });
    }


    public void sendDespawnPacket(Player... players){
        final List<Packet<?>> packets = getDestroyPackets();
        Arrays.stream(players).forEach(player -> PacketUtils.sendPacket(player, packets));
    }

    public void sendDespawnPacket(List<Player> players){
        final List<Packet<?>> packets = getDestroyPackets();
        players.forEach(player -> PacketUtils.sendPacket(player, packets));
    }

    public void updateBodyRotation(){
        getReceivers().forEach(player -> PacketUtils.sendPacket(player, getLocationPacket()));
    }

    public void updateHeadRotation(){
        getReceivers().forEach(player -> PacketUtils.sendPacket(player, getHeadPacket()));
    }

    public int getId(){
        return npc.getId();
    }

    public Set<Player> getReceivers(){
        return receivers;
    }

    public EntityPlayer getNpc(){
        return npc;
    }

    public World getWorld(){
        return world;
    }

    public Location getLocation(){
        return location;
    }

    public String getName(){
        return name;
    }

    public int getReference(){
        return reference;
    }

    public void rotateBody(float yaw){
        setBodyRotation(this.location.getYaw()+yaw);
    }

    public void setBodyRotation(float yaw){
        this.location.setYaw(yaw);
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public float getYawHead(){
        return yawHead;
    }

    public void setYawHeadRotation(float yawHead){
        this.yawHead = yawHead;
    }

    public void rotateYawHead(float yaw){
        setYawHeadRotation(yawHead+yaw);
    }

    protected String[] getSkin(String playerName){
        try{
            final URL urlUUID = new URL("https://api.mojang.com/users/profiles/minecraft/"+playerName);
            final InputStreamReader readerUUID = new InputStreamReader(urlUUID.openStream());
            final String uuid = new JsonParser().parse(readerUUID).getAsJsonObject().get("id").getAsString();

            final URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/"+uuid+"?unsigned=false");
            final InputStreamReader reader = new InputStreamReader(url.openStream());
            final JsonObject property = new JsonParser().parse(reader).getAsJsonObject().get("properties")
                    .getAsJsonArray().get(0).getAsJsonObject();

            final String texture = property.get("value").getAsString();
            final String signature = property.get("signature").getAsString();

            return new String[]{texture, signature};
        }catch(Exception ignored){}
        return null;
    }



    /* TODO: WTF IS THIS
    protected int getOnlinePlayer(){
        try{
            final URL url = new URL("https://api.quillcraft.storagehost.ch/minecraft/server/players/amount");
            final InputStreamReader reader = new InputStreamReader(url.openStream());

            return new JsonParser().parse(reader).getAsJsonObject().getAsInt();
        }catch(Exception err){
            err.printStackTrace();
        }
        return 0;
    }*/


}