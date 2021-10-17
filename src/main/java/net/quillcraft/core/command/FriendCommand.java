package net.quillcraft.core.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.text.Text;
import net.quillcraft.core.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class FriendCommand implements CommandExecutor, TabCompleter {

    private final QuillCraftCore quillCraftCore;
    private final String[] argsCompletion = new String[]{"add", "remove", "list"};
    public FriendCommand(QuillCraftCore quillCraftCore){
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

                    player.sendPluginMessage(quillCraftCore, "quillcraft:friend", out.toByteArray());
                    return true;
                }
                return false;
            }
            if(args.length == 2){
                if(sub.equalsIgnoreCase("add")){
                    if(args[1].equalsIgnoreCase(player.getName())){
                        player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.FRIEND_INVITE_YOUR_SELF));
                        return true;
                    }
                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Add");

                    player.sendPluginMessage(quillCraftCore, "quillcraft:friend", out.toByteArray());
                    return true;
                }
                if(sub.equalsIgnoreCase("remove") || sub.equalsIgnoreCase("rem") ){
                    if(args[1].equalsIgnoreCase(player.getName())){
                        player.sendMessage(LanguageManager.getLanguage(player).getMessage(Text.FRIEND_REMOVE_YOUR_SELF));
                        return true;
                    }
                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Remove");

                    player.sendPluginMessage(quillCraftCore, "quillcraft:friend", out.toByteArray());
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