package com.eleven.celldetection.service.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eleven.celldetection.config.RabbitmqConfig;
import com.eleven.celldetection.entity.Log;
import com.eleven.celldetection.mapper.LogMapper;
import com.eleven.celldetection.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 用户日志表 服务实现类
 * </p>
 *
 * @author Jason
 * @since 2024-05-21
 */
@Service
@Slf4j
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {
    @Autowired
    private LogMapper logMapper;

    @RabbitListener(bindings =  @QueueBinding(
            value = @Queue(name = RabbitmqConfig.QUEUE_LOG),
            exchange = @Exchange(name = RabbitmqConfig.EXCHANGE_TOPIC_LOG, type = ExchangeTypes.TOPIC),
            key = RabbitmqConfig.ROUTINGKEY_LOG
    ))
    public void getLogsFromMQ(@Payload Log myLog){
        System.out.println("===================" + myLog);
    }
}
