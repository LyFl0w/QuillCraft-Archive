package net.quillcraft.lobby.listener.player;

import net.quillcraft.commons.account.Account;
import net.quillcraft.commons.account.AccountProvider;
import net.quillcraft.commons.exception.AccountNotFoundException;
import net.quillcraft.core.manager.LanguageManager;
import net.quillcraft.core.utils.Title;
import net.quillcraft.core.utils.builders.scoreboard.ObjectiveBuilder;
import net.quillcraft.core.utils.builders.scoreboard.ScoreboardBuilder;
import net.quillcraft.lobby.QuillCraftLobby;
import net.quillcraft.lobby.location.LocationEnum;
import net.quillcraft.lobby.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.lumy.api.text.Text;
import org.lumy.api.text.TextList;

public class PlayerJoinListener implements Listener {

    public final static ScoreboardBuilder scoreboardBuilder = new ScoreboardBuilder(QuillCraftLobby.getInstance())
            .addObjective(new ObjectiveBuilder("muguet", "§eTop Chasseur de Muguet", DisplaySlot.SIDEBAR)
                    .addScore(15, "§c")
                    .addScore(14, "1er : ?")
                    .addScore(13, "2eme : ?")
                    .addScore(12, "3eme : ?")
                    .addScore(11, "4eme : ?")
                    .addScore(10, "5eme : ?")
                    .addScore(9, "§f"));

    private final QuillCraftLobby quillCraftLobby;
    public PlayerJoinListener(QuillCraftLobby quillCraftLobby){
        this.quillCraftLobby = quillCraftLobby;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        final Player player = event.getPlayer();
        final BukkitScheduler scheduler = quillCraftLobby.getServer().getScheduler();

        player.teleport(LocationEnum.LOBBY_SPAWN.getLocation());

        scheduler.runTaskAsynchronously(quillCraftLobby, () -> {
            final AccountProvider accountProvider = new AccountProvider(player);
            try{
                final Account account = accountProvider.getAccount();
                final LanguageManager languageManager = LanguageManager.getLanguage(account);

                player.sendMessage(languageManager.getMessage(Text.LOBBY_PLAYER_JOIN).replace("%PLAYER%", player.getDisplayName()));

                if(accountProvider.hasAutoLanguage()){
                    Bukkit.getScheduler().runTaskAsynchronously(quillCraftLobby, () ->
                            quillCraftLobby.getServer().getScheduler().runTask(quillCraftLobby, () -> accountProvider.setLocaleLanguage(account)));
                }


                scheduler.runTask(quillCraftLobby, () -> {
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

                new Title(player).
                        sendTitle(1, 1, 1, languageManager.getMessage(TextList.TITLE_LOBBY_JOIN)).
                        sendTablistTitle(languageManager.getMessage(TextList.TABLIST_DEFAULT));

                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

            }catch(AccountNotFoundException e){
                player.kickPlayer(e.getMessage());
            }
        });

        scheduler.runTaskAsynchronously(quillCraftLobby,
                () -> Bukkit.getOnlinePlayers().stream().parallel()
                        .filter(players -> !players.getUniqueId().equals(player.getUniqueId()))
                        .forEach(players -> players.sendMessage(LanguageManager.getLanguage(players)
                                .getMessage(Text.LOBBY_PLAYER_JOIN).replace("%PLAYER%", player.getDisplayName()))));


        scoreboardBuilder.addPlayer(player);
        scoreboardBuilder.updateScoreboard();

        /* Exemple Song
        final SongManager songManager = quillCraftLobby.getSongManager();
        final Song song = songManager.getSong("overtaken");
        final Song song2 = songManager.getSong("jojo_sound");
        final RadioPlayer radioPlayer = new RadioPlayer(quillCraftLobby, true, true, true, song, song2);
        radioPlayer.addPlayer(player);
        Bukkit.getScheduler().runTaskLater(quillCraftLobby, radioPlayer::playRadio,20L*6);*/

        event.setJoinMessage("");
    }


}