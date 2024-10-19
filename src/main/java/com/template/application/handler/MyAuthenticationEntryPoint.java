package com.template.application.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * ClassName: MyAuthenticationEntryPoint
 * Description: 未登录情况处理逻辑
 *
 * @Author 白给
 * @Create 2024/9/17 14:40
 * @Version 1.0
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        //验证为未登陆状态会进入此方法，认证错误
        response.setStatus(401);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String body = "401, 请先进行登录!";
        printWriter.write(body);
        printWriter.flush();
    }
}
