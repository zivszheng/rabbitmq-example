package com.zivs.rqexample.workfair;

import com.rabbitmq.client.*;
import com.zivs.rqexample.utils.MyConnectionFactory;

import java.io.IOException;

public class Recv1 {
    // 队列名称
    private final static String QUEUE_NAME = "fair_queue";

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
                System.out.println(" [1] Received msg: '" + message + "'");
                // 返回确认状态
                channel.basicAck(envelope.getDeliveryTag(), false);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        };
        // autoAck = true : 自动确认
        // autoAck = false : 手动确认
        channel.basicConsume(QUEUE_NAME, false, consumer);
    }
}