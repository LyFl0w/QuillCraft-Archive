package net.quillcraft.highblock.listener.player;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.database.request.account.AccountRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerDeathListener implements Listener {

    private final HighBlock highblock;

    public PlayerDeathListener(HighBlock highblock) {
        this.highblock = highblock;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final UUID uuid = player.getUniqueId();
        final AccountRequest accountRequest = new AccountRequest(highblock.getDatabase(), false);

        try {
            final float money = accountRequest.getMoney(uuid);
            final float toRemove = (money > 150) ? money * 0.01f : 15;
            accountRequest.setMoney(uuid, money - toRemove);
            highblock.getDatabase().closeConnection();

            player.sendMessage("§cVous avez perdu " + toRemove + "$");
        } catch (SQLException e) {
            highblock.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
