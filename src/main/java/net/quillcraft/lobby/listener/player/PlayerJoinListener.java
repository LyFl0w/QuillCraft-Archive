package net.quillcraft.lobby.listener.player;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.Title;
import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.location.LocationEnum;
import net.quillcraft.lobby.utils.PlayerUtils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.lumy.api.text.Text;
import org.lumy.api.text.TextList;

public class PlayerJoinListener implements Listener {


    private final QuillCraftLobby quillCraftLobby;
    public PlayerJoinListener(QuillCraftLobby quillCraftLobby){
        this.quillCraftLobby = quillCraftLobby;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        final Player player = event.getPlayer();

        quillCraftLobby.getServer().getScheduler().runTaskAsynchronously(quillCraftLobby, () -> {
            final AccountProvider accountProvider = new AccountProvider(player);
            try{
                final Account account = accountProvider.getAccount();
                final LanguageManager languageManager = LanguageManager.getLanguage(account);

                player.sendMessage(languageManager.getMessage(Text.LOBBY_PLAYER_JOIN).replace("%PLAYER%", player.getDisplayName()));

                new Title(player).
                        sendTitle(1, 1, 1, languageManager.getMessage(TextList.TITLE_LOBBY_JOIN)).
                        sendTablistTitle(languageManager.getMessage(TextList.TABLIST_DEFAULT));

                if(accountProvider.hasAutoLanguage()){
                    Bukkit.getScheduler().runTaskAsynchronously(quillCraftLobby, () -> quillCraftLobby.getServer().getScheduler().runTask(quillCraftLobby, () ->
                            accountProvider.setLocaleLanguage(account)));
                }

                Bukkit.getScheduler().runTask(quillCraftLobby, () -> {
                    PlayerUtils.setLobbyParameters(player, account);
                    Bukkit.getOnlinePlayers().forEach(players -> {
                        try{
                            final Account onlinePlayerAccount = new AccountProvider(players).getAccount();
                            onlinePlayerAccount.playVisibilityEffect();
                        }catch(AccountNotFoundException e){
                            e.printStackTrace();
                        }
                    });
                });
            }catch(AccountNotFoundException e){
                player.kickPlayer(e.getMessage());
            }
        });

        /* Exemple Song
        final SongManager songManager = quillCraftLobby.getSongManager();
        final Song song = songManager.getSong("overtaken");
        final Song song2 = songManager.getSong("jojo_sound");
        final RadioPlayer radioPlayer = new RadioPlayer(quillCraftLobby, true, true, true, song, song2);
        radioPlayer.addPlayer(player);
        Bukkit.getScheduler().runTaskLater(quillCraftLobby, radioPlayer::playRadio,20L*6);*/

        player.teleport(LocationEnum.LOBBY_SPAWN.getLocation());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        Bukkit.getOnlinePlayers().stream().parallel().filter(players -> !players.getUniqueId().equals(player.getUniqueId())).forEach(players ->
                players.sendMessage(LanguageManager.getLanguage(players).getMessage(Text.LOBBY_PLAYER_JOIN).replace("%PLAYER%", player.getDisplayName())));

        event.setJoinMessage("");
    }


}