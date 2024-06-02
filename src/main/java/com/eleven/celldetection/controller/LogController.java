package com.eleven.celldetection.controller;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eleven.celldetection.annotation.JwtToken;
import com.eleven.celldetection.config.RabbitmqConfig;
import com.eleven.celldetection.entity.Log;
import com.eleven.celldetection.entity.User;
import com.eleven.celldetection.service.LogService;
import com.eleven.celldetection.utils.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户日志表 前端控制器
 * </p>
 *
 * @author Jason
 * @since 2024-05-21
 */
@RestController
@Slf4j
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private LogService logService;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    @JwtToken()
    @GetMapping("/getLogsByPage/{currentPage}/{pageSize}")
    public Result<PageInfo<Log>> getUsersByPage(@PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize
            ){
        log.info("分页查询User：Page: {} PageSize: {}", currentPage, pageSize);
        PageHelper.startPage(currentPage, pageSize);
        List<Log> logList = logService.list(new QueryWrapper<Log>().eq("status", 1));
        if (logList != null){
            return Result.success(new PageInfo<>(logList));
        }
        return Result.fail(500, "分页查询日志信息失败");
    }

    @JwtToken()
    @PostMapping("/deleteLogs")
    public Result<Log> deleteLogs(@RequestBody List<Integer> ids){
        log.info("删除日志信息：{}", ids);
        if (logService.removeBatchByIds(ids)){
            return Result.success();
        }
        return Result.fail(500, "删除信息失败");
    }

    @JwtToken()
    @GetMapping("/getLogs")
    public Result<List<Log>> getLogsFromMQ(){
        List<Log> logList = logService.list(new QueryWrapper<Log>().eq("status", 0));
        if (logList != null){
            return Result.success(new PageInfo<>(logList).getList());
        }
        return Result.fail(500, "分页查询日志信息失败");
    }

    @JwtToken()
    @PostMapping("/updateStatus")
    public Result<Log> updateStatus(@RequestBody Log log){
        log.setStatus(1);
        System.out.println(log);
        if (logService.updateById(log)){
            return Result.success(log);
        }
        return Result.fail(500, "分页查询日志信息失败");
    }
}
