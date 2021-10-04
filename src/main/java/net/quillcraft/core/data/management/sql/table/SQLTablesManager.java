package net.quillcraft.core.data.management.sql.table;

public enum SQLTablesManager {

    PLAYER_DATA("playerdata", "uuid"),
    PARTY_DATA("partydata", "partyuuid"),
    RANK_DATA("rankdata", "rankid");

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
