package com.zivs.rqexample.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.zivs.rqexample.utils.MyConnectionFactory;
import org.springframework.amqp.core.ExchangeTypes;

public class EmitLogFanout {

    private static final String EXCHANGE_NAME = "fanout_logs";

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = MyConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, ExchangeTypes.FANOUT);

        for (int i = 0; i < 10; i++) {
            String msg = "message " + i;
            channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes());
            System.out.println(" [fanout] Sent msg : " + msg + ".");
        }

        channel.close();
        connection.close();
    }
}