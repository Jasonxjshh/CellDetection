package com.eleven.celldetection.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eleven.celldetection.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author Jason
 * @since 2024-05-08
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);



}
