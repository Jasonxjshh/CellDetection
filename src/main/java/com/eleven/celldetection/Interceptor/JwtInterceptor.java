package com.eleven.celldetection.Interceptor;


import com.eleven.celldetection.annotation.JwtToken;
import com.eleven.celldetection.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;


@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 http 请求头中取出 token
        String token = JwtUtil.parseToken(request.getHeader("token"));
        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(JwtToken.class)){
            JwtToken jwtToken = method.getAnnotation(JwtToken.class);
            if (jwtToken.required()){
                if (token == null){
                    throw new RuntimeException("无token，请重新登录");
                }
                return JwtUtil.checkSign(token);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
