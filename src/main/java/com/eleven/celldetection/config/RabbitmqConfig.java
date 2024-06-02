package com.eleven.celldetection.config;



import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;
@Configuration
public class RabbitmqConfig {
    public static final String QUEUE_LOG = "queue_log";
    public static final String EXCHANGE_TOPIC_LOG = "exchange_topic_log";
    public static final String ROUTINGKEY_LOG = "topic.log";


    // 声明交换机
    @Bean(EXCHANGE_TOPIC_LOG)
    public Exchange EXCHANGE_TOPIC_LOG(){
        // 创建交换机, durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPIC_LOG).durable(true).build();
    }

    // 声明队列
    @Bean(QUEUE_LOG)
    public Queue QUEUE_LOG(){
        return new Queue(QUEUE_LOG);
    }

    @Bean
    public Binding BINDING_ROUTINGKEY_LOG(){
        return BindingBuilder.bind(QUEUE_LOG()).to(EXCHANGE_TOPIC_LOG()).with(ROUTINGKEY_LOG).noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
