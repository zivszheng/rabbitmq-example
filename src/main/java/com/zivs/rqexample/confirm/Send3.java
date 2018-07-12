package com.zivs.rqexample.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.zivs.rqexample.utils.MyConnectionFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author zivs.zheng
 * @description: 异步confirm模式：提供一个回调方法，服务端confirm了一条或者多条消息后Client端会回调这个方法
 * @date 2018/7/12 16:05
 */
public class Send3 {
    // 队列名称
    private final static String QUEUE_NAME = "confirm_queue";

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = MyConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        channel.basicQos(1);

        // 队列声明
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        // 开启 confirm 模式
        channel.confirmSelect();

        for (int i = 0; i < 100000; i++) {
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

        long start = System.currentTimeMillis();
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("nack: deliveryTag = " + deliveryTag + " multiple: " + multiple);
            }

            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("ack: deliveryTag = " + deliveryTag + " multiple: " + multiple);
            }
        });
        System.out.println("执行waitForConfirmsOrDie耗费时间: " + (System.currentTimeMillis() - start) + "ms");

        channel.close();
        connection.close();
    }
}