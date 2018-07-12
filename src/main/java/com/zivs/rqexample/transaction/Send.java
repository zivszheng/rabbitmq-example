package com.zivs.rqexample.transaction;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.zivs.rqexample.utils.MyConnectionFactory;

import java.io.IOException;

/**
 * 开启消息消息队列持久化的sender
 */
public class Send {
    // 队列名称
    private final static String QUEUE_NAME = "transaction_queue";

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = MyConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        /**
         * prefetchCount:会告诉RabbitMQ不要同时给一个消费者推送多于N个消息，即一旦有N个消息还没有ack，则该consumer将block掉，直到有消息ack
         * global:true\false 是否将上面设置应用于channel，简单点说，就是上面限制是channel级别的还是consumer级别
         */
        channel.basicQos(1);

        // 队列声明
        /**
         * durable = true ，开启消息队列持久化
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        /**
         * 开启amqp协议事务
         */
        channel.txSelect();

        for (int i = 0; i < 5; i++) {
            // message
            String message = "User " + i + " Say Hello!";
            // 发送message
            /**
             * MessageProperties.PERSISTENT_TEXT_PLAIN  : 会将 BasicProperties 的 deliveryMode 设置为2
             * deliveryMode=1代表不持久化
             * deliveryMode=2代表持久化
             */
            try {

                channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

                int j = 1 / (i % 2);
                System.out.println(" [" + i + "] Sent msg: '" + message + "'");
                /**
                 * 没有异常，提交事务
                 */
                channel.txCommit();
            } catch (Exception e) {
                /**
                 * 事务回滚
                 */
                channel.txRollback();
                System.err.println("send message rollback msg:" + message);
            }
        }
        channel.close();
        connection.close();
    }
}