package com.template.application.controller.admin;

import com.template.application.common.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 白给
 * @since 2024-07-04
 */
@RestController
@RequestMapping("/api/admin/")
public class AdminController {
    @GetMapping("/")
    public ResponseResult<String> login() {
        return ResponseResult.success("true");
    }

    @GetMapping("/test")
    public ResponseResult<String> test() {
        return ResponseResult.success("test");
    }
}
