package com.classroom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器 + 几个入口地址的跳转。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }

    /**
     * 教师端、学生端、大屏各自有独立网址：
     * /teacher  -> /teacher/home.html
     * /student  -> /student/join.html
     * /screen   -> /screen/index.html
     * /report   -> /report/index.html
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/teacher", "/teacher/home.html");
        registry.addRedirectViewController("/teacher/", "/teacher/home.html");
        registry.addRedirectViewController("/student", "/student/join.html");
        registry.addRedirectViewController("/student/", "/student/join.html");
        registry.addRedirectViewController("/screen", "/screen/index.html");
        registry.addRedirectViewController("/screen/", "/screen/index.html");
        registry.addRedirectViewController("/report", "/report/index.html");
        registry.addRedirectViewController("/report/", "/report/index.html");
    }
}
