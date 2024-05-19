package com.eleven.celldetection.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.eleven.celldetection.utils.BaseContext;
import com.eleven.celldetection.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
//import java.util.Objects;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Autowired
    private HttpServletRequest request;



    @Override
    public void insertFill(MetaObject metaObject) {
        String token = request.getHeader("token");
        Long id = Objects.requireNonNull(JwtUtil.getTockenClaims(JwtUtil.parseToken(token), "id")).asLong();
        log.info("公共字段自动填充[insert]...");
        log.info("线程id：{}", Thread.currentThread().getId());
        this.strictInsertFill(metaObject, "createAt", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateAt", Date.class, new Date());
        this.strictInsertFill(metaObject, "createBy", Long.class, id);
        this.strictInsertFill(metaObject, "updateBy", Long.class, id);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String token = request.getHeader("token");
        log.info("公共字段自动填充[update]...");
        log.info("线程id：{}", Thread.currentThread().getId());
        Long id = Objects.requireNonNull(JwtUtil.getTockenClaims(JwtUtil.parseToken(token), "id")).asLong();
        this.setFieldValByName("updateBy", id, metaObject);
        this.setFieldValByName("updateAt", new Date(), metaObject);
    }
}
