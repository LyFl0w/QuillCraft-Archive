package net.quillcraft.core.command;

import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.text.Text;
import net.quillcraft.core.utils.CommandUtils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class PartyCommand implements CommandExecutor, TabCompleter {

    private final QuillCraftCore quillCraftCore;
    private final String[] argsCompletion = new String[]{"accept", "invite", "kick", "setowner", "list", "leave", "delete"};

    public PartyCommand(QuillCraftCore quillCraftCore){
        this.quillCraftCore = quillCraftCore;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender cmds, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args){
        if(cmds instanceof Player player){

            final String sub = args[0];

            if(args.length == 1){
                if(sub.equalsIgnoreCase("list")){
                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("List");

                    player.sendPluginMessage(quillCraftCore, "quillcraft:party", out.toByteArray());
                    return true;
                }

                if(sub.equalsIgnoreCase("delete")){
                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Delete");

                    player.sendPluginMessage(quillCraftCore, "quillcraft:party", out.toByteArray());
                    return true;
                }

                if(sub.equalsIgnoreCase("leave")){
                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Leave");

                    player.sendPluginMessage(quillCraftCore, "quillcraft:party", out.toByteArray());
                    return true;
                }
                return false;
            }

            if(args.length == 2){
                final String targetPlayerName = args[1];
                if(sub.equalsIgnoreCase("kick")){
                    if(player.getName().equals(targetPlayerName)){
                        player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.PARTY_KICK_YOUR_SELF));
                        return true;
                    }

                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Kick");
                    out.writeUTF(targetPlayerName);

                    player.sendPluginMessage(quillCraftCore, "quillcraft:party", out.toByteArray());
                    return true;
                }

                if(sub.equalsIgnoreCase("accept")){
                    if(player.getName().equals(targetPlayerName)){
                        player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.PARTY_ACCEPT_YOUR_SELF));
                        return true;
                    }

                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();

                    out.writeUTF("Accept");
                    out.writeUTF(targetPlayerName);

                    player.sendPluginMessage(quillCraftCore, "quillcraft:party", out.toByteArray());
                    return true;
                }

                if(sub.equalsIgnoreCase("invite")){
                    if(player.getName().equals(targetPlayerName)){
                        player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.PARTY_INVITE_YOUR_SELF));
                        return true;
                    }

                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();

                    out.writeUTF("Invite");
                    out.writeUTF(targetPlayerName);

                    player.sendPluginMessage(quillCraftCore, "quillcraft:party", out.toByteArray());
                    return true;
                }

                if(sub.equalsIgnoreCase("setowner")){
                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();

                    out.writeUTF("SetOwner");
                    out.writeUTF(targetPlayerName);

                    player.sendPluginMessage(quillCraftCore, "quillcraft:party", out.toByteArray());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender cmds, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args){
        if(cmds instanceof Player){
            if(args.length == 1){
                if(args[0] == null) return null;
                return CommandUtils.completionTable(args[0], argsCompletion);
            }
        }
        return null;
    }

}