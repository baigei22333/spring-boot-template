package com.template.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * ClassName: Application
 * Description:
 *
 * @Author 白给
 * @Create 2024/7/1 20:37
 * @Version 1.0
 */
@EnableAsync    // 开启异步注解支持
@SpringBootApplication
@MapperScan("com.template.application.mapper")  // 扫描mapper文件
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
