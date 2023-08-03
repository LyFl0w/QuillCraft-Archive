package net.quillcraft.lobby.message;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.quillcraft.lobby.QuillCraftLobby;
import org.bukkit.entity.Player;

public abstract class Message {

    protected final QuillCraftLobby quillCraftLobby;

    public Message(QuillCraftLobby quillCraftLobby, byte[] data) {
        this.quillCraftLobby = quillCraftLobby;
        onPluginMessagePlayer(data);
    }

    private void onPluginMessagePlayer(byte[] data) {
        final ByteArrayDataInput in = ByteStreams.newDataInput(data);
        final String sub = in.readUTF();
        final Player player = quillCraftLobby.getServer().getPlayerExact(in.readUTF());

        quillCraftLobby.getServer().getScheduler().runTaskAsynchronously(quillCraftLobby, () -> onPluginMessageRepPlayer(player, sub, in));
    }

    protected abstract void onPluginMessageRepPlayer(Player player, String sub, ByteArrayDataInput in);

}
