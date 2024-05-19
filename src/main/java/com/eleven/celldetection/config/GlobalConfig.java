package com.eleven.celldetection.config;

import com.baomidou.mybatisplus.autoconfigure.DdlApplicationRunner;
import com.eleven.celldetection.Interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author Edwin
 * @version 1.0.0
 */
@Component
public class GlobalConfig implements WebMvcConfigurer {
    @Autowired
    JwtInterceptor jwtInterceptor;

    @Bean
    public DdlApplicationRunner ddlApplicationRunner(@Autowired(required = false) List ddlLrist) {
        return new DdlApplicationRunner(ddlLrist);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**");
    }


}
