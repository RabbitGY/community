package com.gy.community.config;

import com.gy.community.annotation.LoginRequired;
import com.gy.community.controller.interceptor.DataInteceptor;
import com.gy.community.controller.interceptor.LoginRequiredInterceptor;
import com.gy.community.controller.interceptor.LoginTicketInterceptor;
import com.gy.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private DataInteceptor dataInteceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png","/**/*.jpg", "/**/*.jpeg");

//        registry.addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png","/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png","/**/*.jpg", "/**/*.jpeg");
        registry.addInterceptor(dataInteceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png","/**/*.jpg", "/**/*.jpeg");
    }
}
