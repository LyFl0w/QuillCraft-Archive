package net.quillcraft.core.utils;

import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public record Title(Player player) {

    public Title sendTitle(final int fadeIn, final int stay, final int fadeOut, final String message){
        return sendTitle(fadeIn, stay, fadeOut, message, null);
    }

    public Title sendSubtitle(final int fadeIn, final int stay, final int fadeOut, final String message){
        return sendTitle(fadeIn, stay, fadeOut, null, message);
    }

    public Title sendFullTitle(final int fadeIn, final int stay, final int fadeOut, final String title, final String subtitle){
        return sendTitle(fadeIn, stay, fadeOut, title, subtitle);
    }

    public Title sendTitle(final int fadeIn, final int stay, int fadeOut, final String title, final String subtitle){
        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().b;

        playerConnection.a(new ClientboundSetTitlesAnimationPacket(fadeIn*20, stay*20, fadeOut*20));

        if(title != null)
            playerConnection.a(getTitlePacket(EnumTitleAction.TITLE,
                    getChatSerializer(title.replaceAll("%player%", player.getDisplayName()))));

        if(subtitle != null)
            playerConnection.a(getTitlePacket(EnumTitleAction.SUBTITLE,
                    getChatSerializer(subtitle.replaceAll("%player%", player.getDisplayName()))));

        return this;
    }

    public Title sendTablistTitle(final String header, final String footer){
        PacketUtils.sendPacket(player, getTabListPacket(getChatSerializer(header), getChatSerializer(footer)));
        return this;
    }

    public Title sendActionBar(final String message){
        final PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
        connection.a(new PacketPlayOutChat(getChatSerializer(message), ChatMessageType.c, null));
        return this;
    }

    private Packet<PacketListenerPlayOut> getTitlePacket(EnumTitleAction enumTitleAction, final IChatBaseComponent title){
        if(enumTitleAction == EnumTitleAction.TITLE) return new ClientboundSetTitleTextPacket(title);
        return new ClientboundSetSubtitleTextPacket(title);
    }

    private PacketPlayOutPlayerListHeaderFooter getTabListPacket(final IChatBaseComponent header, final IChatBaseComponent footer){
        return new PacketPlayOutPlayerListHeaderFooter(header, footer);
    }

    private IChatBaseComponent getChatSerializer(final String message){
        return IChatBaseComponent.ChatSerializer.a("{\"text\": \""+message+"\"}");
    }

    private enum EnumTitleAction {
        TITLE, SUBTITLE
    }
}