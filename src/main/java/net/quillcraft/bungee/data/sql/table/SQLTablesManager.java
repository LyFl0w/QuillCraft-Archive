package net.quillcraft.bungee.data.sql.table;

public enum SQLTablesManager {

    PLAYER_ACCOUNT("player_account", "uuid"),
    RANK("rank", "rank_id"),
    PARTY("party", "party_uuid"),
    FRIEND("friend", "uuid");

    private final String table, keyColumn;

    SQLTablesManager(String table, String keyColumn) {
        this.table = table;
        this.keyColumn = keyColumn;
    }

    public String getTable() {
        return table;
    }

    public String getKeyColumn() {
        return keyColumn;
    }
}
