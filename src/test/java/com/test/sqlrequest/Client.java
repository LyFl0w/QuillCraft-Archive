package com.test.sqlrequest;

import net.lyflow.sqlrequest.SQLRequest;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private final SQLRequest sqlRequest;
    private String name;
    private int age;

    public Client(int id, String name, int age) {
        this.name = name;
        this.age = age;

        final SQLTablesManager sqlTablesManager = SQLTablesManager.PERSONAL_DATA;
        this.sqlRequest = new SQLRequest(sqlTablesManager.getTable(), sqlTablesManager.getKeyColumn(), Integer.toString(id));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        sqlRequest.addData("name", name);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        sqlRequest.addData("age", age);
    }

    public void updateData() {
        try {
            sqlRequest.sendUpdateRequest(TestRequest.getConnection());
        } catch (Exception exception) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    public void deleteData() {
        try {
            sqlRequest.sendDeleteRequest(TestRequest.getConnection());
        } catch (Exception exception) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, exception.getMessage(), exception);
        }
    }


    // note : getSQLRequest function just for test
    public SQLRequest getSQLRequest() {
        return sqlRequest;
    }

}