package net.quillcraft.commons.party;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    private UUID partyUUID;
    private UUID ownerUUID;
    private List<UUID> followersUUID;
    private List<String> followersName;
    private String ownerName;

    // For Redis
    private Party() {
    }

    public Party(Player player) {
        this(player.getUniqueId(), player.getName());
    }

    public Party(UUID ownerUUID, String ownerNames) {
        this(UUID.randomUUID(), ownerUUID, ownerNames);
    }

    public Party(UUID partyUUID, UUID ownerUUID, String ownerName) {
        this(partyUUID, ownerUUID, ownerName, new ArrayList<>(), new ArrayList<>());
    }

    public Party(UUID partyUUID, UUID ownerUUID, String ownerNames, List<UUID> followersUUID, List<String> membersName) {
        this.partyUUID = partyUUID;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerNames;
        this.followersUUID = followersUUID;
        this.followersName = membersName;
    }

    public UUID getPartyUUID() {
        return partyUUID;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public List<String> getFollowersName() {
        return followersName;
    }

    public List<UUID> getFollowersUUID() {
        return followersUUID;
    }

    public List<UUID> getPlayersUUID() {
        final List<UUID> playersUUID = new ArrayList<>(followersUUID);
        playersUUID.add(ownerUUID);
        return playersUUID;
    }

}
