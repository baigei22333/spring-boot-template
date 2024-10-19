package com.template.application.utils;

import cn.hutool.crypto.SecureUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * ClassName: AesUtil
 * Description:
 *
 * @Author 白给
 * @Create 2024/10/19 16:01
 * @Version 1.0
 */
@Component
public class AesUtil {
    @Value("${aes.secret}")
    private String aesSecret;// AES加密密钥


    /**
     * aes 加密
     *
     * @param str
     * @return
     */
    public String encrypt(String str) {
        return Base64.encodeBase64String(SecureUtil.aes(aesSecret.getBytes(StandardCharsets.UTF_8)).encrypt(str));
    }

    /**
     * aes 解密
     *
     * @param str
     * @return
     */
    public String decrypt(String str) {
        return new String(SecureUtil.aes(aesSecret.getBytes(StandardCharsets.UTF_8)).decrypt(str), StandardCharsets.UTF_8);
    }
}
