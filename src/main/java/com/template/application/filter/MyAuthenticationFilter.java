package com.template.application.filter;

import com.template.application.dto.jwt.AuthenticationToken;
import com.template.application.dto.jwt.JwtAuthenticationToken;
import com.template.application.entity.Root;
import com.template.application.entity.User;
import com.template.application.enums.RoleEnum;
import com.template.application.mapper.RootMapper;
import com.template.application.mapper.UserMapper;
import com.template.application.utils.JwtUtil;
import com.template.application.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * ClassName: MyAuthenticationFilter
 * Description: 安全访问统一入口
 *
 * @Author 白给
 * @Create 2024/9/17 14:44
 * @Version 1.0
 */
@Component
@Slf4j
public class MyAuthenticationFilter extends OncePerRequestFilter {

    @Value("${cors.allowed}")
    private Boolean corsAllowed;

    @Value("${jwt.tokenStart}")
    private String tokenStart;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private RootMapper rootMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization"); // 前后端分离的时候获取token
        log.info("请求头：{}", authHeader);
        log.info("请求路径：{}", request.getRequestURI());
        if (!isInvalidAuthentication(authHeader) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String authToken = authHeader.substring(this.tokenStart.length());
            AuthenticationToken authenticationToken = null;
            try {
                authenticationToken = jwtUtil.getSubjectFromToken(authToken);
            } catch (Exception e) {
                WebUtil.renderString(response, "请先登录");
                return;
            }
            if (!Objects.isNull(authenticationToken)) {
                List<RoleEnum> roles = authenticationToken.getRoles();
                if (roles.contains(RoleEnum.ROOT)) {
                    Root root = rootMapper.selectById(authenticationToken.getId());
                    if (Objects.isNull(root)) {
                        throw new RuntimeException("当前用户不存在或者已被移除");
                    }

                } else if (roles.contains(RoleEnum.USER)) {
                    User user = userMapper.selectById(authenticationToken.getId());

                    if (Objects.isNull(user)) {
                        throw new RuntimeException("当前用户不存在或者已被移除");
                    }
                } else {
                    throw new RuntimeException("Token 错误, 解析不出认证主体对象");
                }
            }

            if (jwtUtil.validateToken(authToken)) {
                // 认证通过，进行授权
                JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
            } else {
                throw new RuntimeException("Token 错误,无法通过token校验");
            }
        }

        /** 放通所有的 options 请求，允许跨域 */
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString()) && corsAllowed) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.setStatus(HttpStatus.OK.value());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Boolean isInvalidAuthentication(String authentication) {
        return StringUtils.isEmpty(authentication) || !authentication.startsWith(this.tokenStart);
    }
}
