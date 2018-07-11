package com.zivs.rqexample.fanout;

import com.rabbitmq.client.*;
import com.zivs.rqexample.utils.MyConnectionFactory;
import org.springframework.amqp.core.ExchangeTypes;

import java.io.IOException;

public class ReceiveLogsFanout2 {

    private static final String EXCHANGE_NAME = "fanout_logs";

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = MyConnectionFactory.getConnection();
        Channel channel = connection.createChannel();

        // 声明direct类型转发器
        channel.exchangeDeclare(EXCHANGE_NAME, ExchangeTypes.FANOUT);
        // 随机生成一个队列
        String queueName = channel.queueDeclare().getQueue();

        // 指定routingKey [error]
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        // 实时监听mq消息处理
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [fanout] Received msg: '" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}