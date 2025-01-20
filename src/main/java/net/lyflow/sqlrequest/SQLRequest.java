package net.lyflow.sqlrequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLRequest {

    private static final Logger logger = Logger.getLogger(SQLRequest.class.getName());

    private HashMap<String, Object> data;
    private String key;
    private String keyColumn;
    private String table;

    public SQLRequest() {
    }

    public SQLRequest(String table, String keyColumn, String key) {
        this.table = table;
        this.keyColumn = keyColumn;
        this.key = key;
        this.data = new HashMap<>();
    }

    public SQLRequest(SQLRequest sqlRequest) {
        this(sqlRequest.table, sqlRequest.keyColumn, sqlRequest.key);
    }

    public SQLRequest addData(String columnName, Object value) {
        if (value instanceof UUID) value = value.toString();

        if (data.containsKey(columnName)) {
            data.replace(columnName, value);
            return this;
        }
        data.put(columnName, value);
        return this;
    }

    public void sendUpdateRequest(Connection connection) throws SQLException {
        getUpdateRequest(connection).executeUpdate();
        connection.close();
    }

    public void sendDeleteRequest(Connection connection) throws SQLException {
        getDeleteRequest(connection).execute();
        connection.close();
    }

    public PreparedStatement getUpdateRequest(Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = setDataUpdate(connection.prepareStatement(buildUpdateRequest()));
        clear();
        return preparedStatement;
    }

    public PreparedStatement getDeleteRequest(Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = setDataDelete(connection.prepareStatement(buildDeleteRequest()));
        clear();
        return preparedStatement;
    }

    public Map<String, Object> getCloneData() {
        return new HashMap<>(data);
    }

    private String buildUpdateRequest() {
        final StringBuilder request = new StringBuilder().append("UPDATE ").append(table).append(" SET");

        final AtomicInteger index = new AtomicInteger();
        data.keySet().forEach(columnName -> {
            request.append(" ").append(columnName).append(" = ?");

            if (index.incrementAndGet() != data.size()) request.append(",");
        });

        return request.append(" WHERE ").append(keyColumn).append(" = ?").toString();
    }

    private String buildDeleteRequest() {
        return "DELETE FROM " + table + " WHERE " + keyColumn + " = ?";
    }

    private PreparedStatement setDataUpdate(PreparedStatement preparedStatement) {
        try {
            final AtomicInteger i = new AtomicInteger(0);
            data.values().forEach(value -> {
                try {
                    final int iAff = i.incrementAndGet();
                    preparedStatement.setObject(iAff, value);
                } catch (SQLException exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                }
            });
            preparedStatement.setObject(i.incrementAndGet(), key);

            return preparedStatement;
        } catch (Exception exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        throw new AssertionError("The data failed to be added to your request");
    }

    private PreparedStatement setDataDelete(PreparedStatement preparedStatement) {
        try {
            preparedStatement.setObject(1, key);
            return preparedStatement;
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        throw new AssertionError("The data could not be deleted");
    }

    public void clear() {
        data.clear();
    }

}