package com.eleven.celldetection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eleven.celldetection.annotation.JwtToken;
import com.eleven.celldetection.dto.UserLoginDTO;
import com.eleven.celldetection.entity.User;
import com.eleven.celldetection.mapper.UserMapper;
import com.eleven.celldetection.service.UserService;
import com.eleven.celldetection.utils.BaseContext;
import com.eleven.celldetection.utils.JwtUtil;
import com.eleven.celldetection.utils.Result;
import com.eleven.celldetection.vo.UserLoginVO;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Jason
 * @since 2024-05-08
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @JwtToken(required = false)
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("用户登录：{}", userLoginDTO);
        User user = userService.login(userLoginDTO);
        String token = JwtUtil.sign(user.getId(), user.getUsername(), user.getRole());
        BaseContext.setCurrentUser(user);
        log.info(BaseContext.getCurrentUser().toString());
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .name(user.getName())
                .role(user.getRole())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }

    private UserMapper userMapper ;

    @JwtToken
    @GetMapping("/getUSerByToken")
    public Result<User> getUSerByToken(@PathParam("token") String token){
        String parseToken = JwtUtil.parseToken(token);
        Long id = Objects.requireNonNull(JwtUtil.getTockenClaims(parseToken, "id")).asLong();
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("id", id) ;
//        User user =  userMapper.select(queryWrapper) ;
        User user = userService.getById(id);
        if (user != null){
            return Result.success(user);
        }else {
            return Result.fail("用户不存在");
        }
    }

}
