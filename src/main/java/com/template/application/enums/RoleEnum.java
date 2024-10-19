package com.template.application.enums;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * ClassName: RoleEnum
 * Description:
 *
 * @Author 白给
 * @Create 2024/7/4 21:19
 * @Version 1.0
 */
@Getter
public enum RoleEnum {
    ROOT("root", "管理员"),
    USER("user", "用户");

    private final String value;
    private final String description;

    RoleEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 基于value查找枚举
     *
     * @param value
     * @return
     */
    public static RoleEnum fromValue(String value) {
        return Arrays.stream(RoleEnum.values())
                .filter(role -> role.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("没有找到" + value + "所对应的枚举值"));
    }
}
