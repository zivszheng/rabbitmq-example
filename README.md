# rabbitmq-example

### 项目介绍

> rabbitmq 用例

- **dependency**
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```
- **content**
  - **Queun**
  - **Exchange** [ *direct, fanout, topic* ]
  - **Durable queun**
  - **Messages acknowledged**
  - **Tranaction**
  - **Confirm**
  
  
 ### RabbitMQ 的消息确认机制（事务 + confirm）

> 在rabbitmq中，我们可也通过持久化数据解决rabbitmq服务异常的数据丢失问题。

> 但是在生产者将消息发送出去之后，消息是否到达rabbitmq服务器，默认情况是不知道的。

> 为此有两种解决方式：

- **AMQP事务机制**
- **confirm模式**


 #### 1、AMQP事务机制

- **txSelect**：开启事务，即用户将当前channel设置成transaction模式
- **txCommit**：事务提交
- **txRollback**：事务回滚

> 缺点：此种方式降低了rabbitmq的消息吞吐量

 #### 2、confirm模式

> 生产者将channel设置成confirm模式，一旦channel进入confirm模式，所有在该channel上面发布的消息都将会被指派一个唯一的ID(从1开始)，一旦消息被投递到所有匹配的队列之后，broker就会发送一个确认给生产者(包含消息的唯一ID)，这就使得生产者知道消息已经正确到达目的队列了，如果消息和队列是可持久化的，那么确认消息会在将消息写入磁盘之后发出，broker回传给生产者的确认消息中delivery-tag域包含了确认消息的序列号，此外broker也可以设置basic.ack的multiple域，表示到这个序列号之前的所有消息都已经得到了处理；

> confirm模式最大的好处在于他是异步的，一旦发布一条消息，生产者应用程序就可以在等channel返回确认的同时继续发送下一条消息，当消息最终得到确认之后，生产者应用便可以通过回调方法来处理该确认消息，如果RabbitMQ因为自身内部错误导致消息丢失，就会发送一条nack消息，生产者应用程序同样可以在回调方法中处理该nack消息；

- **开启confirm模式的方法**：[ *channel.confirmSelect()* ]

   > 生产者通过调用channel的confirmSelect方法将channel设置为confirm模式，(注意一点，已经在transaction事务模式的channel是不能再设置成confirm模式的，即这两种模式是不能共存的)，如果没有设置no-wait标志的话，broker会返回confirm.select-ok表示同意发送者将当前channel设置为confirm模式(从目前RabbitMQ最新版本3.6来看，如果调用了channel.confirmSelect方法，默认情况下是直接将no-wait设置成false的，也就是默认情况下broker是必须回传confirm.select-ok的，而且我也没找到我们自己能够设置no-wait标志的方法)；
- **生产者实现confiem模式有三种编程方式**：

    - **普通confirm模式**：每发送一条消息，调用waitForConfirms()方法等待服务端confirm，这实际上是一种串行的confirm，每publish一条消息之后就等待服务端confirm，如果服务端返回false或者超时时间内未返回，客户端进行消息重传；

    - **批量confirm模式**：每发送一批消息之后，调用waitForConfirms()方法，等待服务端confirm，这种批量确认的模式极大的提高了confirm效率，但是如果一旦出现confirm返回false或者超时的情况，客户端需要将这一批次的消息全部重发，这会带来明显的重复消息，如果这种情况频繁发生的话，效率也会不升反降；

    - **异步confirm模式**：提供一个回调方法，服务端confirm了一条或者多条消息后Client端会回调这个方法；
