package net.quillcraft.core.event.player;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.core.QuillCraftCore;
import net.quillcraft.core.manager.LanguageManager;

import net.quillcraft.core.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nonnull;

public class PlayerChangeLanguageEvent extends PlayerEvent implements Cancellable {

    private boolean isCancelled = false;

    private final String languageISO;
    private final AccountProvider accountProvider;
    private final Account account;

    public PlayerChangeLanguageEvent(final Player player, final AccountProvider accountProvider, final Account account, final String languageISO){
        super(player);
        this.accountProvider = accountProvider;
        this.account = account;
        this.languageISO = languageISO;
        final LanguageManager languageManager = LanguageManager.getLanguageByISO(languageISO);

        if(!languageManager.getISO().equalsIgnoreCase(LanguageManager.getLanguage(account).getISO())){
            this.account.setLanguage(languageManager.getISO());
            this.accountProvider.updateAccount(this.account);
            player.sendMessage(languageManager.getMessage(Text.COMMAND_SETLANGUAGE_SELECTED));
            accountProvider.updateLanguage();
            return;
        }
        player.sendMessage(languageManager.getMessage(Text.COMMAND_SETLANGUAGE_SAME));

        setCancelled(true);
    }

    public String getNewLanguageISO(){
        return languageISO;
    }

    public AccountProvider getAccountProvider(){
        return accountProvider;
    }

    public Account getAccount(){
        return account;
    }

    @Override
    public boolean isCancelled(){
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean setCancelled){
        isCancelled = setCancelled;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @Nonnull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
