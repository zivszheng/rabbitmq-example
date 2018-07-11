package com.zivs.rqexample.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.zivs.rqexample.utils.MyConnectionFactory;
import org.springframework.amqp.core.ExchangeTypes;

public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = MyConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, ExchangeTypes.TOPIC);

        String[] routing_keys = {"kernel.info", "cron.warn", "auth.info", "kernel.critical"};
        int i = 0;
        for (String routing_key : routing_keys) {
            String msg = "message " + i++;
            channel.basicPublish(EXCHANGE_NAME, routing_key, null, msg.getBytes());
            System.out.println(" [" + routing_key + "] Sent msg : " + msg + ".");
        }

        channel.close();
        connection.close();
    }
}