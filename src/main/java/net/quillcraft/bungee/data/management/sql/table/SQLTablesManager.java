package net.quillcraft.bungee.data.management.sql.table;

public enum SQLTablesManager {

    PLAYER_DATA("playerdata", "uuid"),
    RANK_DATA("rankdata", "rankid"),
    PARTY_DATA("partydata", "partyuuid");

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
