package com.classroom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }


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
