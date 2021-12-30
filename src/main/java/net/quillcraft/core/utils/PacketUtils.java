package net.quillcraft.core.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PacketUtils {

    public static void sendPacket(List<Player> players, Packet<?> packets){
        players.stream().parallel().forEach(player -> sendPacket(player, packets));
    }

    public static void sendPacket(Player player, Packet<?> packet){
        ((CraftPlayer)player).getHandle().b.a(packet);
    }

    public static void sendPacket(List<Player> players, List<Packet<?>> packets){
        players.stream().parallel().forEach(player -> sendPacket(player, packets));
    }

    public static void sendPacket(Player player, List<Packet<?>> packets){
        final PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().b;

        packets.forEach(playerConnection::a);
    }

}
