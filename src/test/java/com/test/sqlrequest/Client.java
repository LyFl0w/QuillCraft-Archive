package com.test.sqlrequest;

import net.lyflow.sqlrequest.SQLRequest;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Client {
    private String name;
    private int age;
    private final SQLRequest sqlRequest;

    public Client(int id, String name, int age){
        this.name = name;
        this.age = age;

        final SQLTablesManager sqlTablesManager = SQLTablesManager.PERSONAL_DATA;
        this.sqlRequest = new SQLRequest(sqlTablesManager.getTable(), sqlTablesManager.getKeyColumn(), Integer.toString(id));
    }

    public void setName(String name){
        this.name = name;
        sqlRequest.addData("name", name);
    }

    public void setAge(int age){
        this.age = age;
        sqlRequest.addData("age", age);
    }

    public String getName(){
        return name;
    }

    public int getAge(){
        return age;
    }

    public void updateData(){
        try{
            Class.forName("com.mys.jdbc.Driver");
            sqlRequest.sendUpdateRequest(DriverManager.getConnection("jdbc:mysql://localhost:3306/clientbase", "user", "pass"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}