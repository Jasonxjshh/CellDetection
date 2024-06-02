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
        Long id = null;

        if (token != null) {
            try {
                id = Objects.requireNonNull(JwtUtil.getTokenClaims(JwtUtil.parseToken(token), "id")).asLong();
            } catch (Exception e) {
                // 处理 JWT 解析错误，可能是无效的 token 或 token 中不包含 "id" 字段
                id = null;
            }
        }

        log.info("公共字段自动填充[insert]...");
        log.info("线程id：{}", Thread.currentThread().getId());

        // 自动填充公共字段
        this.strictInsertFill(metaObject, "createAt", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateAt", Date.class, new Date());

        // 如果 id 不为空，填充 createBy 和 updateBy 字段
        if (id != null) {
            this.strictInsertFill(metaObject, "createBy", Long.class, id);
            this.strictInsertFill(metaObject, "updateBy", Long.class, id);
        } else {
            // 处理没有用户 ID 的情况，可以使用默认值或特殊标识
            this.strictInsertFill(metaObject, "createBy", Long.class, 0L); // 使用 0 表示未登录用户
            this.strictInsertFill(metaObject, "updateBy", Long.class, 0L);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String token = request.getHeader("token");
        Long id = null;

        if (token != null) {
            try {
                id = Objects.requireNonNull(JwtUtil.getTokenClaims(JwtUtil.parseToken(token), "id")).asLong();
            } catch (Exception e) {
                // 处理 JWT 解析错误，可能是无效的 token 或 token 中不包含 "id" 字段
                id = null;
            }
        }

        log.info("公共字段自动填充[update]...");
        log.info("线程id：{}", Thread.currentThread().getId());

        // 自动填充 updateBy 字段
        if (id != null) {
            this.setFieldValByName("updateBy", id, metaObject);
        } else {
            // 处理没有用户 ID 的情况，可以使用默认值或特殊标识
            this.setFieldValByName("updateBy", 0L, metaObject); // 使用 0 表示未登录用户
        }

        this.setFieldValByName("updateAt", new Date(), metaObject);
    }
}
