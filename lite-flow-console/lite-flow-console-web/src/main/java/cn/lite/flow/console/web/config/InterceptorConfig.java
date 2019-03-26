package cn.lite.flow.console.web.config;

import cn.lite.flow.console.web.interceptor.AuthCheckInterceptor;
import cn.lite.flow.console.web.interceptor.AuthUrlInterceptor;
import cn.lite.flow.console.web.interceptor.ConsoleInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by ly on 2018/12/24.
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public ConsoleInterceptor loginInterceptor() {
        return new ConsoleInterceptor();
    }

    @Bean
    public AuthCheckInterceptor authCheckInterceptor() {
        return new AuthCheckInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/console/**", "/executor/**")
                .excludePathPatterns("/console/login");

        registry.addInterceptor(authCheckInterceptor())
                .addPathPatterns("/console/**", "/executor/**")
                .excludePathPatterns("/console/login");

    }
}
