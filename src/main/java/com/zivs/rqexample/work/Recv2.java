package com.zivs.rqexample.work;

import com.rabbitmq.client.*;
import com.zivs.rqexample.utils.MyConnectionFactory;

import java.io.IOException;

public class Recv2 {
    // 队列名称
    private final static String QUEUE_NAME = "rabbit_zivs_work";

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = MyConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        // 队列声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 实时监听mq消息处理
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [2] Received msg: '" + message + "'");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);

    }
}