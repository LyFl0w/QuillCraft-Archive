package com.test.sqlrequest;

import net.lyflow.sqlrequest.SQLRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class TestRequest {

    private static Connection connection = null;

    private static final SQLTablesManager TABLE = SQLTablesManager.PERSONAL_DATA;

    private static final int clientIDTest = 12;
    private static final Client client = new Client(clientIDTest, "Daniel", 23);

    public static Connection getConnection() throws SQLException {
        if(connection == null || connection.isClosed())
            connection = DriverManager.getConnection("jdbc:h2:./src/test/resources/data/sample", "username", "password");

        return connection;
    }

    @BeforeAll
    static void createDatabase() throws SQLException {
        final Connection connection = getConnection();
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE.getTable() + " (" + TABLE.getKeyColumn() + " INT PRIMARY KEY, name VARCHAR(255), age INT)");
    }

    @AfterAll
    static void deleteDatabase() throws SQLException {
        final Connection connection = getConnection();
        Statement statement = connection.createStatement();
        statement.execute("DROP TABLE IF EXISTS " + TABLE.getTable());

        connection.close();
    }

    private void addClient() throws SQLException {
        final Connection connection = getConnection();
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO " + TABLE.getTable() + " VALUES ('" + clientIDTest + "', '" + client.getName() + "', '" + client.getAge() + "')");
    }

    @Test
    void updateClient() throws SQLException {
        addClient();

        client.setAge(24);
        client.setName("Bernard");
        client.updateData();

        // TEST if a client has been created with the name Benard and age 23
        // Verify if the client has been added correctly by querying the database

        // Execute a query to retrieve information about the added client
        Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE.getTable() + " WHERE " + TABLE.getKeyColumn() + " = " + clientIDTest);

        // Verify if the query result meets expectations
        assertTrue(resultSet.next(), "No record found for the added client.");

        // Compare retrieved values with expected ones
        assertEquals(clientIDTest, resultSet.getInt(TABLE.getKeyColumn()), "Incorrect ID");
        assertEquals(client.getName(), resultSet.getString("name"), "Incorrect Name");
        assertEquals(client.getAge(), resultSet.getInt("age"), "Incorrect Age");

        // Close resources
        resultSet.close();
    }

    @Test
    void deleteClient() throws SQLException {
        // Test Delete Data
        client.deleteData();
        // Execute a query to retrieve information about the added client

        Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE.getTable() + " WHERE " + TABLE.getKeyColumn() + " = " + clientIDTest);

        // Verify if the query result meets expectations
        assertFalse(resultSet.next(), "record found for the added client.");

        resultSet.close();
    }

    @Test
    void checkDataClone() {
        final SQLRequest sqlRequest = client.getSQLRequest();
        assertNotSame(sqlRequest.getCloneData(), sqlRequest.getCloneData(), "Data is not clone");
    }
}
