package com.cjh.watching.watchback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 创建请求匹配器
        RequestMatcher registerMatcher = new AntPathRequestMatcher("/auth/register", "POST");
        RequestMatcher loginMatcher = new AntPathRequestMatcher("/auth/login", "POST");
        
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(registerMatcher, loginMatcher).permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable()); // 禁用CSRF保护
        
        return http.build();
    }
}