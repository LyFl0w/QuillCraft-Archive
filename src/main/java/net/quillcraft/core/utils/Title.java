package net.quillcraft.core.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class Title {

    private final Player player;

    public Title(Player player) {
        this.player = player;
    }

    public Title sendTitle(final int fadeIn, final int stay, final int fadeOut, final String message) {
        return sendTitle(fadeIn, stay, fadeOut, message, null);
    }

    public Title sendSubtitle(final int fadeIn, final int stay, final int fadeOut, final String message) {
        return sendTitle(fadeIn, stay, fadeOut, null, message);
    }

    public Title sendFullTitle(final int fadeIn, final int stay, final int fadeOut, final String title, final String subtitle) {
        return sendTitle(fadeIn, stay, fadeOut, title, subtitle);
    }

    public Title sendTitle(final int fadeIn, final int stay, int fadeOut, final List<String> titles) {
        return sendTitle(fadeIn, stay, fadeOut, titles.get(0), titles.get(1));
    }

    public Title sendTitle(final int fadeIn, final int stay, int fadeOut, final String title, final String subtitle) {
        final ServerGamePacketListenerImpl playerConnection = ((CraftPlayer) player).getHandle().connection;

        playerConnection.send(new ClientboundSetTitlesAnimationPacket(fadeIn * 20, stay * 20, fadeOut * 20));

        if (title != null)
            playerConnection.send(getTitlePacket(EnumTitleAction.TITLE, getChatSerializer(title.replace("%player%", player.getDisplayName()))));

        if (subtitle != null)
            playerConnection.send(getTitlePacket(EnumTitleAction.SUBTITLE, getChatSerializer(subtitle.replace("%player%", player.getDisplayName()))));

        return this;
    }

    public Title sendTablistTitle(final String header, final String footer) {
        PacketUtils.sendPacket(player, getTabListPacket(getChatSerializer(header), getChatSerializer(footer)));
        return this;
    }

    public Title sendTablistTitle(final List<String> tablist) {
        return sendTablistTitle(tablist.get(0), tablist.get(1));
    }

    public Title sendActionBar(final String message) {
        final ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(new ClientboundSystemChatPacket(getChatSerializer(message), true));
        return this;
    }

    private Packet<ClientGamePacketListener> getTitlePacket(EnumTitleAction enumTitleAction, final Component title) {
        if (enumTitleAction == EnumTitleAction.TITLE) return new ClientboundSetTitleTextPacket(title);
        return new ClientboundSetSubtitleTextPacket(title);
    }

    private ClientboundTabListPacket getTabListPacket(final Component header, final Component footer) {
        return new ClientboundTabListPacket(header, footer);
    }

    private Component getChatSerializer(final String message) {
        return Component.Serializer.fromJson("{\"text\": \"" + message + "\"}");
    }

    private enum EnumTitleAction {
        TITLE, SUBTITLE
    }
}