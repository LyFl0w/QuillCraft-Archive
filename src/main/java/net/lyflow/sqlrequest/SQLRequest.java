package net.lyflow.sqlrequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SQLRequest implements Cloneable{

    private String keyColumn, table;
    private Object key;
    private HashMap<String, Object> data;

    //Default Constructor (for Redis to construct instance)
    public SQLRequest(){}

    public SQLRequest(String table, String keyColumn, Object key){
        this.keyColumn = keyColumn;
        this.key = key;
        this.table = table;
        this.data = new HashMap<>();
    }

    public SQLRequest addData(String columnName, Object value){
        if(value instanceof UUID) value = value.toString();

        if(data.containsKey(columnName)){
            data.replace(columnName, value);
            return this;
        }
        data.put(columnName, value);
        return this;
    }

    public void sendUpdateRequest(Connection connection) throws SQLException{
        getUpdateRequest(connection).executeUpdate();
        connection.close();
    }

    public void sendDeleteRequest(Connection connection) throws SQLException{
        getDeleteRequest(connection).execute();
        connection.close();
    }

    public PreparedStatement getUpdateRequest(Connection connection) throws SQLException{
        final PreparedStatement preparedStatement = setDataUpdate(connection.prepareStatement(buildUpdateRequest()));
        clear();
        return preparedStatement;
    }

    public PreparedStatement getDeleteRequest(Connection connection) throws SQLException{
        final PreparedStatement preparedStatement = setDataDelete(connection.prepareStatement(buildDeleteRequest()));
        clear();
        return preparedStatement;
    }

    private String buildUpdateRequest(){
        final StringBuilder request = new StringBuilder().append("UPDATE ").append(table).append(" SET");

        final AtomicInteger index = new AtomicInteger();
        data.keySet().forEach(key -> {
            request.append(" ").append(key).append(" = ?");

            if(index.incrementAndGet() != data.size()) request.append(",");
        });

        return request.append(" WHERE ").append(keyColumn).append(" = ?").toString();
    }

    private String buildDeleteRequest(){
        return "DELETE FROM "+table+" WHERE "+keyColumn+" = ?";
    }

    private PreparedStatement setDataUpdate(PreparedStatement preparedStatement){
        try{
            final AtomicInteger i = new AtomicInteger(0);
            data.values().forEach(value -> {
                try{
                    int iAff = i.incrementAndGet();
                    preparedStatement.setObject(iAff, value);
                    System.out.println(iAff+" = i, "+value+" = entry\n");
                }catch(SQLException exception){
                    exception.printStackTrace();
                }
            });
            preparedStatement.setObject(i.incrementAndGet(), key);

            return preparedStatement;
        }catch(Exception exception){
            exception.printStackTrace();
        }
        throw new AssertionError("The data failed to be added to your request");
    }

    private PreparedStatement setDataDelete(PreparedStatement preparedStatement){
        try{
            preparedStatement.setObject(1, key);
            return preparedStatement;
        }catch(SQLException exception){
            exception.printStackTrace();
        }
        throw new AssertionError("The data could not be deleted");
    }

    public void clear(){
        data.clear();
    }

    @Override
    public SQLRequest clone(){
        try{
            return (SQLRequest) super.clone();
        }catch(CloneNotSupportedException e){
            throw new AssertionError("Clone of the SQLRequest class has been done wrong");
        }
    }
}
