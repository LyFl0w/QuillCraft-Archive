package net.quillcraft.highblock.listener.player;

import net.quillcraft.highblock.HighBlock;
import net.quillcraft.highblock.challenge.Challenge;
import net.quillcraft.highblock.challenge.PlayerChallengeProgress;
import net.quillcraft.highblock.command.LobbyCommand;
import net.quillcraft.highblock.database.request.account.AccountRequest;
import net.quillcraft.highblock.database.request.challenge.ChallengeRequest;
import net.quillcraft.highblock.database.request.island.IslandRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class PlayerJoinListener implements Listener {

    private final HighBlock highblock;

    public PlayerJoinListener(HighBlock highblock) {
        this.highblock = highblock;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        final BukkitScheduler scheduler = highblock.getServer().getScheduler();
        scheduler.runTaskAsynchronously(highblock, () -> scheduler.runTask(highblock, () -> {
            final AccountRequest accountRequest = new AccountRequest(highblock.getDatabase(), false);
            try {
                final boolean hasAccount = accountRequest.hasAccount(uuid);
                if (!hasAccount) {
                    player.teleport(LobbyCommand.spawn);
                    accountRequest.createPlayerAccount(player);

                    event.setJoinMessage("§6Bienvenue à §b" + player.getName() + " §6!");
                } else {
                    accountRequest.updatePlayerName(player);
                    if (!new IslandRequest(highblock.getDatabase(), false).hasIsland(uuid))
                        player.teleport(LobbyCommand.spawn);
                }

                final ChallengeRequest challengeRequest = new ChallengeRequest(highblock.getDatabase(), false);
                final Map<Integer, String> currentChallengesSerialized = challengeRequest.getChallengesDataSerialized(uuid);
                final ArrayList<Integer> currentChallengesID = new ArrayList<>(currentChallengesSerialized.keySet());
                final List<Challenge<? extends Event>> actualChallenges = highblock.getChallengeManager().getRegisteredChallenges();

                // ADD NEW CHALLENGES IN HASHMAP TO INIT THEM
                final HashMap<Integer, PlayerChallengeProgress> dataToSave = new HashMap<>();

                actualChallenges.stream().parallel().filter(challenge -> !currentChallengesID.contains(challenge.getID())).forEach(challenge ->
                        dataToSave.put(challenge.getID(), challenge.getChallengeProgress().initPlayerChallenge(player)));

                // INIT NEW CHALLENGES
                if (!dataToSave.isEmpty()) {
                    final int playerID = accountRequest.getPlayerID(player);
                    final Connection connection = highblock.getDatabase().getConnection();
                    final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Challenge VALUES (?, ?, ?)");

                    connection.setAutoCommit(false);
                    dataToSave.forEach((key, value) -> {
                        try {
                            preparedStatement.setInt(1, key);
                            preparedStatement.setInt(2, playerID);
                            preparedStatement.setString(3, value.serialize());

                            preparedStatement.addBatch();
                        } catch (SQLException e) {
                            throw new IllegalCallerException(e);
                        }
                    });

                    preparedStatement.executeBatch();
                    connection.commit();

                    player.sendMessage((hasAccount) ? "§6De nouveaux challenges sont disponibles !" : "§6Pour accéder à vos challenge utiliser la commande §e/challenge");
                }

                highblock.getDatabase().closeConnection();

                // LOAD THE CHALLENGE DATA IF IT ISN'T ALREADY AVAILABLE
                actualChallenges.stream().parallel().filter(challenge -> !challenge.getChallengeProgress().getPlayersCounter().containsKey(uuid))
                        .forEach(challenge -> challenge.getChallengeProgress().loadPlayerChallenge(uuid,
                                PlayerChallengeProgress.deserialize(currentChallengesSerialized.get(challenge.getID()))));

            } catch (SQLException e) {
                throw new IllegalCallerException(e);
            }
        }));
    }

}