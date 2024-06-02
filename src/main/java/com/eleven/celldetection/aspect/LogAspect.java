package com.eleven.celldetection.aspect;


import com.alibaba.fastjson.JSON;
import com.eleven.celldetection.config.RabbitmqConfig;
import com.eleven.celldetection.entity.Log;
import com.eleven.celldetection.mapper.LogMapper;
import com.eleven.celldetection.mapper.UserMapper;
import com.eleven.celldetection.utils.JwtUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.Join;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Aspect
public class LogAspect {

    @Autowired
    private HttpServletRequest request;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;







    @Pointcut("@annotation( com.eleven.celldetection.annotation.Log )")
    public void logPointCut(){}


    @Before("logPointCut()")
    public void beforeLogPoint(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        String token = request.getHeader("token");
        Long id = Objects.requireNonNull(JwtUtil.getTokenClaims(JwtUtil.parseToken(token), "id")).asLong();
        String name = userMapper.selectById(id).getName();

        StringBuffer url = request.getRequestURL();
        log.info(request.getPathInfo());
        log.info(request.getPathTranslated());
        log.info(request.getContextPath());
        log.info(request.getRemoteHost());
        log.info(String.valueOf(request.getServerPort()));
        String ip = request.getRemoteAddr();
        String agent = request.getHeader("User-Agent");
        Log userLog = Log.builder().userId(id).userName(name).url(url.toString()).ip(ip).agent(agent).build();
        log.info("向RabbitMQ发送用户请求日志：{}", userLog);
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPIC_LOG, "topic.log", userLog);
        log.info("用户日志：{}", userLog);
        logMapper.insert(userLog);
    }
}
