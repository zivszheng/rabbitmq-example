package com.zivs.rqexample.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.zivs.rqexample.utils.MyConnectionFactory;

import java.io.IOException;
import java.sql.CallableStatement;
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

        // 未确认的消息集合
        SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<>());

        // channel 添加监听
        channel.addConfirmListener(new ConfirmListener() {
            // 处理法发送有问题的
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                if(multiple) {
                    confirmSet.headSet(deliveryTag+1).clear();
                    System.out.println("nack: deliveryTag = " + deliveryTag + " multiple: " + multiple);
                } else {
                    confirmSet.remove(deliveryTag);
                    System.err.println("nack: deliveryTag = " + deliveryTag + " multiple: " + multiple);
                }
            }

            // 处理发送成功的
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                if(multiple) {
                    confirmSet.headSet(deliveryTag+1).clear();
                    System.out.println("ack: deliveryTag = " + deliveryTag + " multiple: " + multiple);
                } else {
                    confirmSet.remove(deliveryTag);
                    System.err.println("ack: deliveryTag = " + deliveryTag + " multiple: " + multiple);
                }
            }
        });

        String message ;
        while (true) {
            Long seq = channel.getNextPublishSeqNo();

            // 发送message
            message = "User " + seq + " Say Hello!";
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println(" [" + seq + "] Sent msg: '" + message + "'");

            confirmSet.add(seq);
        }
    }
}