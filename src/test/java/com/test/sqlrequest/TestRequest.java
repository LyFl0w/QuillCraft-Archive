package com.test.sqlrequest;

public class TestRequest {

    public static void main(String[] args){
        final Client client = new Client(12, "Daniel", 23);
        client.setAge(24);
        client.updateData();
    }


}
