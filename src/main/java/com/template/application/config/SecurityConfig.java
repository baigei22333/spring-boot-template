package com.template.application.config;

import com.template.application.constant.SecurityConstant;
import com.template.application.enums.RoleEnum;
import com.template.application.handler.MyAccessDeniedHandler;
import com.template.application.handler.MyAuthenticationEntryPoint;
import com.template.application.filter.MyAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * ClassName: SecurityConfig
 * Description:
 *
 * @Author 白给
 * @Create 2024/7/5 21:35
 * @Version 1.0
 */

@Configuration
@EnableWebSecurity //开启SpringSecurity的默认行为
// 新版不需要继承WebSecurityConfigurerAdapter
@RequiredArgsConstructor //注入Bean
public class SecurityConfig {

    /**
     * 权限不足处理逻辑
     */
    private final MyAccessDeniedHandler accessDeniedHandler;

    /**
     * 未授权处理逻辑
     */
    private final MyAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 访问统一处理器
     */
    private final MyAuthenticationFilter authenticationTokenFilter;


    // 创建BCryptPasswordEncoder 注入容器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // 认证管理器
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(HttpMethod.GET,
                                        "swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-resources/**").permitAll()
                                .requestMatchers(SecurityConstant.WHITE_LIST).permitAll()
                                .requestMatchers("/api/admin/**").hasAnyRole(RoleEnum.ROOT.getValue())
                                .requestMatchers("/api/user/**").hasAnyRole(RoleEnum.USER.getValue())
                                //  允许所有 OPTIONS 请求 放行的接口
                                .anyRequest()
                                .authenticated()
                )
                // 基于token 不需要csrf
                .csrf(AbstractHttpConfigurer::disable)
                //  基于 token ， 不需要 session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 禁用basic明文验证
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(exception ->
                        exception
                                // 访问异常处理
                                .accessDeniedHandler(accessDeniedHandler)
                ).exceptionHandling(exception ->
                        exception
                                // 未授权异常处理
                                .authenticationEntryPoint(authenticationEntryPoint)
                )
                .headers(headers ->
                        headers
                                // 禁用缓存
                                .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                ).addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加入口filter， 前后端分离的时候，可以进行token解析操作;
        return http.build();
    }
}
