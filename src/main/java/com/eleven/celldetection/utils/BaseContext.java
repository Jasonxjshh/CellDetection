package com.eleven.cellcommon.utils;

import com.eleven.cellpojo.entity.User;

public class BaseContext {
    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();
 
    /**
     * 设置值
     * @param user
     */
    public static void setCurrentUser(User user){
        threadLocal.set(user);
    }
 
    /**
     * 获取值
     * @return
     */
    public static User getCurrentUser(){
        return threadLocal.get();
    }
}