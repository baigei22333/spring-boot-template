package com.template.application.controller.user;

import com.github.xiaoymin.knife4j.annotations.Ignore;
import com.template.application.common.ResponseResult;
import com.template.application.dto.jwt.AuthenticationToken;
import com.template.application.dto.request.user.LoginUserReq;
import com.template.application.entity.User;
import com.template.application.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 白给
 * @since 2024-07-04
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private UserService userService;


    @PostMapping("/login")
    public ResponseResult<String> login(@RequestBody LoginUserReq loginUserReq) {
        String token = userService.login(loginUserReq);
        return ResponseResult.success(token);
    }

    @GetMapping("/info")
    public ResponseResult<User> getInfo(@Ignore @AuthenticationPrincipal AuthenticationToken authenticationToken) {
        Integer id = authenticationToken.getId();
        User user = userService.getInfo(id);
        return ResponseResult.success(user);
    }
}
