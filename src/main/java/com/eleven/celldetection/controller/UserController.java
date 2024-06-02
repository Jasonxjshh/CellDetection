package com.eleven.celldetection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eleven.celldetection.annotation.JwtToken;
import com.eleven.celldetection.annotation.Log;
import com.eleven.celldetection.dto.UserLoginDTO;
import com.eleven.celldetection.entity.User;
import com.eleven.celldetection.mapper.UserMapper;
import com.eleven.celldetection.service.UserService;
import com.eleven.celldetection.utils.BaseContext;
import com.eleven.celldetection.utils.JwtUtil;
import com.eleven.celldetection.utils.Result;
import com.eleven.celldetection.vo.UserLoginVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        String token = JwtUtil.sign(user.getId(), user.getRole());
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

    @JwtToken(required = false)
    @PostMapping("/register")
    public Result<UserLoginVO> register(@RequestBody User user){
        log.info("用户注册：{}", user);
        User newUser = userService.doRegister(user);
        String token = JwtUtil.sign(newUser.getId(), newUser.getRole());
        BaseContext.setCurrentUser(newUser);
        log.info(BaseContext.getCurrentUser().toString());
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(newUser.getId())
                .userName(newUser.getUsername())
                .name(newUser.getName())
                .role(newUser.getRole())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }

    @JwtToken
    @GetMapping("/getUSerByToken")
    public Result<User> getUSerByToken(@PathParam("token") String token){
        String parseToken = JwtUtil.parseToken(token);
        Long id = Objects.requireNonNull(JwtUtil.getTokenClaims(parseToken, "id")).asLong();
        User user = userService.getById(id);
        if (user != null){
            return Result.success(user);
        }else {
            return Result.fail("用户不存在");
        }
    }


    @JwtToken()
    @Log
    @PostMapping("/update")
    public Result<User> update(@RequestBody User user){
        log.info("修改用户信息：{}", user);
        if (user != null) {
            if (userService.updateById(user)){
                User newUser = userService.getById(user.getId());
                BaseContext.setCurrentUser(newUser);
                return Result.success(newUser);
            }
        }
        return Result.fail(500, "修改用户信息失败");
    }


    @JwtToken()
    @PostMapping("/add")
    @Log
    public Result<User> addUser(@RequestBody User user){
        log.info("新增用户信息：{}", user);
        if (user != null) {
            if (userService.save(user)){
                return Result.success();
            }
        }
        return Result.fail(500, "新增用户信息失败");
    }

    @JwtToken()
    @GetMapping("/getUsersByPage/{currentPage}/{pageSize}/{role}")
    public Result<PageInfo<User>> getUsersByPage(@PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize
            , @PathVariable("role") Integer role){
        log.info("分页查询User：Page: {} PageSize: {} Role: {}", currentPage, pageSize, role);
        PageHelper.startPage(currentPage, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", role);
        List<User> userByPage = userService.list(queryWrapper);
        if (userByPage != null){
            return Result.success(new PageInfo<>(userByPage));
        }
        return Result.fail(500, "分页查询用户信息失败");
    }

    @GetMapping("/getUsersByID/{currentPage}/{pageSize}/{id}")
    public Result<PageInfo<User>> getUsersByID(@PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize
            , @PathVariable("id") Integer id){
        log.info("分页查询User：Page: {} PageSize: {} id: {}", currentPage, pageSize, id);
        PageHelper.startPage(currentPage, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        List<User> userByPage = userService.list(queryWrapper);
        if (userByPage != null){
            return Result.success(new PageInfo<>(userByPage));
        }
        return Result.fail(500, "分页查询用户信息失败");
    }


    @JwtToken()
    @PostMapping("/deleteUsers")
    @Log
    public Result<User> deleteUsers(@RequestBody List<Integer> ids){
        log.info("删除用户信息：{}", ids);
        if (userService.removeBatchByIds(ids)){
            return Result.success();
        }
        return Result.fail(500, "新增用户信息失败");
    }
}
