package com.test.sqlrequest;

public enum SQLTablesManager {

    PERSONAL_DATA("clientdata", "id");

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