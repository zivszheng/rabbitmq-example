package com.zivs.rqexample.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.zivs.rqexample.utils.MyConnectionFactory;

/**
 * @description: 批量confirm模式：每发送一批消息之后，调用waitForConfirms()方法，等待服务端confirm，这种批量确认的模式极大的提高了confirm效率
 * @author zivs.zheng
 * @date 2018/7/12 16:05
 */
public class Send2 {
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
        for (int i = 0; i < 5; i++) {
            // message
            String message = "User " + i + " Say Hello!";
            // 发送message
            /**
             * MessageProperties.PERSISTENT_TEXT_PLAIN  : 会将 BasicProperties 的 deliveryMode 设置为2
             * deliveryMode=1代表不持久化
             * deliveryMode=2代表持久化
            */
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println(" [" + i + "] Sent msg: '" + message + "'");
        }
        if(!channel.waitForConfirms()){
            System.out.println("send message failed .");
        }

        channel.close();
        connection.close();
    }
}