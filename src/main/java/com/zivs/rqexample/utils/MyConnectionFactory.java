package com.zivs.rqexample.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MyConnectionFactory {

    private static Connection connection;

    public static Connection getConnection() throws IOException, TimeoutException {
        if (null == connection) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("127.0.0.1");
            connection = factory.newConnection();
        }
        return connection;
    }
}
