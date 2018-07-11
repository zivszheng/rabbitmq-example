package com.zivs.rqexample.routing;

import com.rabbitmq.client.*;
import com.zivs.rqexample.utils.MyConnectionFactory;

import java.io.IOException;

public class ReceiveLogsDirect {

    private static final String EXCHANGE_NAME = "ex_logs_direct";

    private static final String[] SEVERITIES = {"info", "warning", "error"};

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = MyConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        // 声明direct类型转发器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        // 随机生成一个队列
        String queueName = channel.queueDeclare().getQueue();

        // 指定routingKey [info] , [warn]
        channel.queueBind(queueName, EXCHANGE_NAME, "info");
        channel.queueBind(queueName, EXCHANGE_NAME, "warn");

        // 实时监听mq消息处理
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [" + envelope.getRoutingKey() + "] Received msg: '" + message + "'");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}