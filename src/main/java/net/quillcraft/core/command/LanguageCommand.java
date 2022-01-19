package net.quillcraft.core.command;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.event.player.PlayerChangeLanguageEvent;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.utils.CommandUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.lumy.api.text.Text;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageCommand implements CommandExecutor, TabCompleter {

    private final QuillCraftCore quillCraftCore;
    private final static String languages = "§cLanguage values : AUTO, "+Arrays.stream(LanguageManager.values()).parallel().map(Object::toString).collect(Collectors.joining(", "));;

    public LanguageCommand(final QuillCraftCore quillCraftCore){
        this.quillCraftCore = quillCraftCore;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(commandSender instanceof final Player player){
            final AccountProvider accountProvider = new AccountProvider(player);
            try{
                final Account account = accountProvider.getAccount();
                final LanguageManager accountLanguage = LanguageManager.getLanguage(account);

                if(accountProvider.hasUpdatedLanguage()){
                    player.sendMessage(accountLanguage.getMessage(Text.SPAM));
                    return true;
                }

                if(args.length == 1){
                    final String arg = args[0];
                    if(arg.equalsIgnoreCase("AUTO")){
                        player.sendMessage(accountLanguage.getMessage(Text.COMMAND_SETLANGUAGE_AUTO));
                        quillCraftCore.getServer().getPluginManager().callEvent(new PlayerChangeLanguageEvent(player, accountProvider, account,
                                player.getLocale()));
                        return true;
                    }
                    Arrays.stream(LanguageManager.values()).parallel().forEach(language -> {

                    });
                    for(LanguageManager languageManager : LanguageManager.values()){
                        if(languageManager.name().equalsIgnoreCase(arg)){
                            quillCraftCore.getServer().getPluginManager().callEvent(new PlayerChangeLanguageEvent(player, accountProvider, account,
                                    languageManager.getISO()));
                            return true;
                        }
                    }
                    player.sendMessage(languages);
                    return true;
                }
            }catch(AccountNotFoundException e){
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args){
        if(commandSender instanceof Player){
            if(args.length == 1){
                if(args[0] == null) return null;
                return CommandUtils.completionTable(args[0], LanguageManager.values(), Collections.singletonList("AUTO"));
            }
        }
        return null;
    }
}