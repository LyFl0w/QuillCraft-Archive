package net.quillcraft.lobby.player;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.lobby.inventory.InventoryLobby;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import net.quillcraft.lumy.api.text.Text;

import javax.annotation.Nonnull;

public class PlayerVisibilityChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final AccountProvider accountProvider;
    private final Account.Visibility visibility;
    private boolean isCancelled = false;

    public PlayerVisibilityChangeEvent(final Player player, final Account.Visibility visibility) throws AccountNotFoundException {
        this(player, new AccountProvider(player), visibility);
    }

    public PlayerVisibilityChangeEvent(final Player player, final AccountProvider accountProvider, final Account.Visibility visibility) throws AccountNotFoundException {
        this(player, accountProvider, accountProvider.getAccount(), visibility);
    }

    public PlayerVisibilityChangeEvent(final Player player, final AccountProvider accountProvider, final Account account, final Account.Visibility visibility) {
        this.player = player;
        this.accountProvider = accountProvider;
        this.visibility = visibility;

        final LanguageManager languageManager = LanguageManager.getLanguage(account);

        if(accountProvider.hasUpdatedVisibility()) {
            player.sendMessage(languageManager.getMessage(Text.SPAM));
            setCancelled(true);
            return;
        }

        if(account.getVisibility().equals(visibility)) {
            player.sendMessage(languageManager.getMessage(Text.ITEMS_INVENTORY_LOBBY_VISIBILITY_SAME));
            setCancelled(true);
            return;
        }

        account.setVisibility(visibility);
        accountProvider.updateAccount(account);

        accountProvider.updateVisibility();

        account.playVisibilityEffect();
        InventoryLobby.updateVisibility(player.getInventory(), visibility, languageManager);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public final Player getPlayer() {
        return player;
    }

    public final AccountProvider getAccountProvider() {
        return accountProvider;
    }

    public final Account.Visibility getVisibility() {
        return visibility;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean setCancelled) {
        isCancelled = setCancelled;
    }

    @Nonnull
    public HandlerList getHandlers() {
        return getHandlerList();
    }


}
