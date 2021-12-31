package net.quillcraft.core.data.management.sql.table;

public enum SQLTablesManager {

    PLAYER_ACCOUNT("player_account", "uuid"),
    RANK("rank", "rank_id"),
    FRIEND("friend","uuid");

    private final String table, keyColumn;

    SQLTablesManager(String table, String keyColumn){
        this.table = table;
        this.keyColumn = keyColumn;
    }

    public String getTable(){
        return table;
    }

    public String getKeyColumn(){
        return keyColumn;
    }
}
