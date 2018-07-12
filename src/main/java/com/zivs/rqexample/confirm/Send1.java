package com.zivs.rqexample.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.zivs.rqexample.utils.MyConnectionFactory;

/**
 * @description: 普通confirm模式：每发送一条消息，调用waitForConfirms()方法等待服务端confirm，这实际上是一种串行的confirm
 * @author zivs.zheng
 * @date 2018/7/12 16:04
 */
public class Send1 {
    // 队列名称
    private final static String QUEUE_NAME = "confirm_queue";

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
         * 开启 confirm 模式
         */
        channel.confirmSelect();

        // message
        String message = "User Say Hello!";
        // 发送message
        /**
         * MessageProperties.PERSISTENT_TEXT_PLAIN  : 会将 BasicProperties 的 deliveryMode 设置为2
         * deliveryMode=1代表不持久化
         * deliveryMode=2代表持久化
        */
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        if(!channel.waitForConfirms()){
            System.out.println("send message failed msg: '" + message + "'");
        } else {
            System.out.println(" [single] Sent msg: '" + message + "'");
        }

        channel.close();
        connection.close();
    }
}