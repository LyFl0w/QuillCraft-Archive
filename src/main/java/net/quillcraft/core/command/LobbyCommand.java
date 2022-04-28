package net.quillcraft.core.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.quillcraft.core.QuillCraftCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class LobbyCommand implements CommandExecutor{

    private final byte[] bungeeCordMessage;
    private final QuillCraftCore quillCraftCore;
    public LobbyCommand(final QuillCraftCore quillCraftCore){
        this.quillCraftCore = quillCraftCore;

        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("lobby_1");
        bungeeCordMessage = out.toByteArray();
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(commandSender instanceof final Player player){
            player.sendMessage("Vous allez être téléporté au lobby");
            player.sendPluginMessage(quillCraftCore, "BungeeCord", bungeeCordMessage);
            return true;
        }
        return false;
    }
}
