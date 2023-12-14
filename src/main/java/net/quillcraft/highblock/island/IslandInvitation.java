package net.quillcraft.highblock.island;

import java.util.UUID;

public record IslandInvitation(UUID playerUUID, int islandID, String targetPlayerName) {}
