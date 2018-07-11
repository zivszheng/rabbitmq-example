package com.zivs.rqexample.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.zivs.rqexample.utils.MyConnectionFactory;

public class Send {
    // 队列名称
    private final static String QUEUE_NAME = "rabbit_zivs_simple";

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = MyConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        // 队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        for (int i = 0; i < 50; i++) {
            // message
            String message = "User " + i + " Say Hello World!";
            // 发送message
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

            System.out.println(" [x] Sent msg: '" + message + "'");
        }

        channel.close();
        connection.close();
    }

} 