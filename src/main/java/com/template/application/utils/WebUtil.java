package com.template.application.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static cn.hutool.core.util.CharsetUtil.UTF_8;

/**
 * ClassName: WebUtils
 * Description:
 *
 * @Author 白给
 * @Create 2024/7/15 21:37
 * @Version 1.0
 */
public class WebUtil {
    public static String renderString(HttpServletResponse response, String str) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding(UTF_8);
            response.getWriter().println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
