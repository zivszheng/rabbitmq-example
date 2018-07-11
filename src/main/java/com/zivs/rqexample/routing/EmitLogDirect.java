package com.zivs.rqexample.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.zivs.rqexample.utils.MyConnectionFactory;

import java.util.Random;

public class EmitLogDirect {

    private static final String EXCHANGE_NAME = "ex_logs_direct";

    private static final String[] SEVERITIES = {"info", "warn", "error"};

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = MyConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        // 声明转发器的类型
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //发送6条消息
        for (int i = 0; i < 10; i++) {
            String routingKey = getSeverity();
            String message = "message " + i;
            // 发布消息至转发器，指定 routingKey
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            System.out.println(" [" + routingKey + "] Sent '" + message + "'");
        }

        channel.close();
        connection.close();
    }

    /**
     * 随机产生一种日志类型
     *
     * @return
     */
    private static String getSeverity() {
        Random random = new Random();
        int ranVal = random.nextInt(3);
        return SEVERITIES[ranVal];
    }

}