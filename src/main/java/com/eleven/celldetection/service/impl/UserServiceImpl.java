package com.eleven.service.impl;

import com.eleven.entity.User;
import com.eleven.mapper.UserMapper;
import com.eleven.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
