package com.eleven.celldetection.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.eleven.celldetection.dto.UserLoginDTO;
import com.eleven.celldetection.entity.User;
import com.github.pagehelper.PageInfo;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Jason
 * @since 2024-05-08
 */
public interface UserService extends IService<User> {

    User login(UserLoginDTO userDTO);

    PageInfo<User> getUserByPage(int currentPage, int pageSize, int role);

    User doRegister(User user);
}
