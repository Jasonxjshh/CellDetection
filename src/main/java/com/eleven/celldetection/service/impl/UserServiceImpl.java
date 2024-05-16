package com.eleven.celldetection.service.impl;


import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eleven.celldetection.dto.UserLoginDTO;
import com.eleven.celldetection.entity.User;
import com.eleven.celldetection.exception.BaseException;
import com.eleven.celldetection.mapper.UserMapper;
import com.eleven.celldetection.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Jason
 * @since 2024-05-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        User user = userMapper.selectByUsername(username);

        if (user == null){
            throw new BaseException("用户不存在");
        }
        if (!StringUtils.equals(password, user.getPassword())){
            throw new BaseException("密码错误");
        }
        if (user.getRole() != userLoginDTO.getRole()){
            throw new BaseException("权限不符");
        }

        return user;
    }
}
