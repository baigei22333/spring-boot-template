package com.template.application.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.template.application.dto.jwt.AuthenticationToken;
import com.template.application.enums.RoleEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: JwtUtils
 * Description:
 *
 * @Author 白给
 * @Create 2024/7/4 20:16
 * @Version 1.0
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;// 加密密钥


    @Resource
    private AesUtil aesUtil;


    private static final int EXPIRE_TIME = 7; //到期时间 7天

    private static final String CLAIM_KEY_ID = "sub";          // 认证主体ID
    private static final String CLAIM_KEY_ROLE = "role";       // 权限，使用AES加密

    private static final String SEPARATOR = ":";                // 分离符号

    /**
     * 生成token令牌
     *
     * @param subject 主题(用户类型)
     * @return token
     */
    private String generateToken(String subject) {
        SecretKey signKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        DateTime now = DateTime.now();
        DateTime endDate = DateUtil.offsetDay(now, EXPIRE_TIME);
        return Jwts.builder()
                .signWith(signKey, Jwts.SIG.HS256)
                .header().add("type", "JWT").and()
                .issuedAt(now)   // 发行时间
                .subject(subject)
                .expiration(endDate)    // 过期时间
                .compact();
    }

    /**
     * 生成token令牌
     *
     * @param claims  JWT第二部分负载 payload 中存储的内容
     * @param subject 主题(用户类型)
     * @return token
     */
    private String generateToken(Map<String, Object> claims, String subject) {
        SecretKey signKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        DateTime now = DateTime.now();
        DateTime endDate = DateUtil.offsetDay(now, EXPIRE_TIME);
        return Jwts.builder()
                .signWith(signKey, Jwts.SIG.HS256)
                .header().add("type", "JWT").and()
                .issuedAt(now)   // 发行时间
                .subject(subject)
                .expiration(endDate)    // 过期时间
                .claims(claims)
                .compact();
    }

    /**
     * 生成token令牌
     *
     * @param claims
     * @return
     */
    private String generateToken(Map<String, Object> claims) {
        SecretKey signKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        DateTime now = DateTime.now();
        DateTime endDate = DateUtil.offsetDay(now, EXPIRE_TIME);
        return Jwts.builder()
                .signWith(signKey, Jwts.SIG.HS256)
                .header().add("type", "JWT").and()
                .issuedAt(now)   // 发行时间
                .expiration(endDate)    // 过期时间
                .claims(claims)
                .compact();
    }

    /**
     * 生成token令牌
     *
     * @param millis  过期时间 单位：毫秒
     * @param claims  JWT第二部分负载 payload 中存储的内容
     * @param subject 主题(用户类型)
     * @return token
     */
    private String generateToken(Map<String, Object> claims, String subject, int millis) {
        SecretKey signKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        DateTime now = DateTime.now();
        DateTime endDate = DateUtil.offsetMillisecond(now, millis);
        return Jwts.builder()
                .signWith(signKey, Jwts.SIG.HS256)
                .header().add("type", "JWT").and()
                .issuedAt(now)   // 发行时间
                .subject(subject)
                .expiration(endDate)    // 过期时间
                .claims(claims)
                .compact();
    }

    /**
     * 普通用户生成token
     *
     * @param userId
     * @return
     */
    public String generateToken(Integer userId) {
        Map<String, Object> claims = new HashMap<>();

        // 设置id
        claims.put(CLAIM_KEY_ID, Integer.toString(userId));
        // 设置权限

        System.out.println(RoleEnum.USER.getValue() + SEPARATOR + userId);
        System.out.println(aesUtil.encrypt(RoleEnum.USER.getValue() + SEPARATOR + userId));
        claims.put(CLAIM_KEY_ROLE, aesUtil.encrypt(RoleEnum.USER.getValue() + SEPARATOR + userId));

        return generateToken(claims);
    }

    /**
     * 管理员生成token
     *
     * @param adminId
     * @return
     */
    public String generateAdminToken(Integer adminId) {
        Map<String, Object> claims = new HashMap<>();

        // 设置id
        claims.put(CLAIM_KEY_ID, Integer.toString(adminId));
        // 设置权限
        claims.put(CLAIM_KEY_ROLE, aesUtil.encrypt(RoleEnum.ROOT.getValue() + SEPARATOR + adminId));

        return generateToken(claims);
    }


    /**
     * 验证token令牌
     *
     * @param token 令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        JWT jwt = JWT.of(token);
        long exp = Long.parseLong(jwt.getPayload().getClaim("exp").toString());
        DateTime expTime = DateTime.of(exp * 1000);
        return expTime.after(DateTime.now());

    }

    /**
     * 从令牌中获取主题信息
     *
     * @param token 令牌
     * @return 身份验证令牌
     */
    public AuthenticationToken getSubjectFromToken(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        JWTPayload payload = jwt.getPayload();
        String id = payload.getClaim(CLAIM_KEY_ID).toString();
        String AESRole = payload.getClaim(CLAIM_KEY_ROLE).toString();

        AuthenticationToken authenticationToken = new AuthenticationToken();
        String role = aesUtil.decrypt(AESRole).split(SEPARATOR)[0];
        RoleEnum roleEnum = RoleEnum.fromValue(role);

        authenticationToken.setToken(token);
        authenticationToken.setId(Integer.parseInt(id));
        authenticationToken.getRoles().clear();
        authenticationToken.getRoles().add(roleEnum);
        return authenticationToken;
    }


}
