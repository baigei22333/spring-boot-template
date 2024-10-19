package com.template.application.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ClassName: LoginUserReq
 * Description:
 *
 * @Author 白给
 * @Create 2024/7/29 22:19
 * @Version 1.0
 */
@Data
public class LoginUserReq {
    @NotNull
    private String userName;

    @NotNull
    private String password;
}
