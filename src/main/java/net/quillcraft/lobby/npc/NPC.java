package net.quillcraft.lobby.npc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.quillcraft.core.utils.PacketUtils;
import net.quillcraft.lobby.QuillCraftLobby;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

public class NPC {

    private static final String TEXTURE_FIELD = "textures";

    private final Set<Player> receivers;

    private final ServerPlayer npcPlayer;
    private final String name;
    private final int reference;
    private World world;
    private Location location;
    private SynchedEntityData dataWatcher;
    private float yawHead;

    private NPC(String name, int reference, Location location, float yawHead, GameProfile gameProfile) {
        this.receivers = new HashSet<>();
        this.name = name;
        this.reference = reference;
        this.yawHead = yawHead;

        final DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
        final ServerLevel worldServer = ((CraftWorld) location.getWorld()).getHandle();

        final ClientInformation info = ClientInformation.createDefault();

        this.npcPlayer = new ServerPlayer(server, worldServer, gameProfile, info);

        final Connection c = new Connection(PacketFlow.SERVERBOUND);
        c.channel = new EmbeddedChannel();
        c.channel.attr(Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(ConnectionProtocol.PLAY.codec(PacketFlow.CLIENTBOUND));
        c.channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.PLAY.codec(PacketFlow.SERVERBOUND));
        c.address = new InetSocketAddress("localhost", 0);

        new ServerGamePacketListenerImpl(server, c, npcPlayer, new CommonListenerCookie(gameProfile, 0, info));

        this.npcPlayer.getBukkitEntity().setPlayerListName("§8[NPC] §f"+ gameProfile.getName());
    }

    protected NPC(String name, List<String> skinPart, int reference, Location location, float yawHead, GameProfile gameProfile) {
        this(name, reference, location, yawHead, gameProfile);

        if(skinPart != null && skinPart.size() == 2) {
            gameProfile.getProperties().put(TEXTURE_FIELD, new Property(TEXTURE_FIELD, skinPart.get(0), skinPart.get(1)));

            //DISPLAY 3D PART OF THE SKIN
            setDataWatcher();
        }

        setLocation(location);
        updateBodyRotation();
    }

    protected NPC(String name, String skinName, int reference, Location location, GameProfile gameProfile) {
        this(name, reference, location, location.getYaw(), gameProfile);

        if(!skinName.isBlank()) {
            final String[] skinsType = getSkin(skinName);
            if(skinsType != null) {
                gameProfile.getProperties().put(TEXTURE_FIELD, new Property(TEXTURE_FIELD, skinsType[0], skinsType[1]));

                //DISPLAY 3D PART OF THE SKIN
                setDataWatcher();
            }
        }
        setLocation(location);
        updateBodyRotation();
    }

    private void setDataWatcher() {
        dataWatcher = npcPlayer.getEntityData();
        dataWatcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 0xFF);
    }

    public NPC addReceiver(List<Player> players) {
        this.receivers.addAll(players);
        return this;
    }

    public NPC addReceiver(Player player) {
        this.receivers.add(player);
        return this;
    }

    public NPC removeReceiver(List<Player> players) {
        players.forEach(this.receivers::remove);
        return this;
    }

    public NPC removeReceiver(Player player) {
        this.receivers.remove(player);
        return this;
    }

    public boolean isReceiver(Player player) {
        return this.receivers.contains(player);
    }

    private ArrayList<Packet<?>> getSpawnPackets() {
        final ArrayList<Packet<?>> packets = new ArrayList<>();
        //packets.add(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc)); //Display NPC
        packets.add(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npcPlayer)); //Display NPC
        packets.add(new ClientboundAddEntityPacket(npcPlayer)); //Display NPC

        //packets.add(new ServerboundClientInformationPacket(npcPlayer.clientInformation())); // Display NPC

        if(dataWatcher != null)
            packets.add(new ClientboundSetEntityDataPacket(getId(), dataWatcher.getNonDefaultValues())); // Display 3D part of the Skin

        packets.add(getHeadPacket()); // Rotate head
        packets.add(new ClientboundAnimatePacket(npcPlayer, 0)); // Rotate Correctly the body of NPC with left-click

        return packets;
    }

    private ClientboundRotateHeadPacket getHeadPacket() {
        return new ClientboundRotateHeadPacket(npcPlayer, (byte) (yawHead*256/360));
    }

    private ClientboundTeleportEntityPacket getLocationPacket() {
        return new ClientboundTeleportEntityPacket(npcPlayer);
    }

    private ArrayList<Packet<?>> getDestroyPackets() {
        final ArrayList<Packet<?>> packets = new ArrayList<>();
        packets.add(new ClientboundRemoveEntitiesPacket(getId()));

        return packets;
    }

    public void sendSpawnPacket(Player... players) {
        final List<Packet<?>> packets = getSpawnPackets();

        Arrays.stream(players).forEach(player -> PacketUtils.sendPacket(player, packets));
    }

    public void sendDespawnPacket(Player... players) {
        final List<Packet<?>> packets = getDestroyPackets();
        Arrays.stream(players).forEach(player -> PacketUtils.sendPacket(player, packets));
    }

    public void sendDespawnPacket(List<Player> players) {
        final List<Packet<?>> packets = getDestroyPackets();
        players.forEach(player -> PacketUtils.sendPacket(player, packets));
    }

    public void updateBodyRotation() {
        getReceivers().forEach(player -> PacketUtils.sendPacket(player, getLocationPacket()));
    }

    public void updateHeadRotation() {
        getReceivers().forEach(player -> PacketUtils.sendPacket(player, getHeadPacket()));
    }

    public int getId() {
        // npc.hashCode() is generally equals to its ID
        return npcPlayer.getId();
    }

    public Set<Player> getReceivers() {
        return receivers;
    }

    public ServerPlayer getNpcPlayer() {
        return npcPlayer;
    }

    public World getWorld() {
        return world;
    }

    public Location getLocation() {
        return location;
    }

    public NPC setLocation(Location location) {
        this.location = location;
        this.world = location.getWorld();

        npcPlayer.forceSetPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        npcPlayer.setYHeadRot(location.getYaw());
        return this;
    }

    public String getName() {
        return name;
    }

    public int getReference() {
        return reference;
    }

    public void rotateBody(float yaw) {
        setBodyRotation(this.location.getYaw()+yaw);
    }

    public void setBodyRotation(float yaw) {
        this.location.setYaw(yaw);
        npcPlayer.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public float getYawHead() {
        return yawHead;
    }

    public void setYawHeadRotation(float yawHead) {
        this.yawHead = yawHead;
    }

    public void rotateYawHead(float yaw) {
        setYawHeadRotation(yawHead+yaw);
    }

    protected String[] getSkin(String playerName) {
        try {
            final URL urlUUID = new URL("https://api.mojang.com/users/profiles/minecraft/"+playerName);
            final InputStreamReader readerUUID = new InputStreamReader(urlUUID.openStream());

            final String uuid = JsonParser.parseReader(readerUUID).getAsJsonObject().get("id").getAsString();

            final URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/"+uuid+"?unsigned=false");
            final InputStreamReader reader = new InputStreamReader(url.openStream());
            final JsonObject property = JsonParser.parseReader(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            final String texture = property.get("value").getAsString();
            final String signature = property.get("signature").getAsString();

            return new String[]{texture, signature};
        } catch(Exception exception) {
            QuillCraftLobby.getInstance().getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }
        return new String[0];
    }

}