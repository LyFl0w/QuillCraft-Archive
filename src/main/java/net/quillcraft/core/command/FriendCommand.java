package net.quillcraft.core.command;

import net.quillcraft.core.QuillCraftCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class FriendCommand implements CommandExecutor, TabCompleter {

    private final QuillCraftCore quillCraftCore;
    public FriendCommand(QuillCraftCore quillCraftCore){
        this.quillCraftCore = quillCraftCore;
    }


    @Override
    public boolean onCommand(@Nonnull CommandSender cmds, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args){
        if(cmds instanceof Player player){

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender cmds, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args){
        if(cmds instanceof Player player){
        }
        return null;
    }
}
