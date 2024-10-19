package com.template.application.dto.jwt;

import com.template.application.enums.RoleEnum;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ClassName: AuthenticationToken
 * Description:
 *
 * @Author 白给
 * @Create 2024/7/4 20:48
 * @Version 1.0
 */
@Data
public class AuthenticationToken {

    // id
    private Integer id;

    /**
     * 认证对象的 token
     */
    private String token;

    // 角色
    private List<RoleEnum> roles = new ArrayList<>();


    /**
     * 获取权限
     * @return
     */
    public Collection<? extends GrantedAuthority> getAuthorities(){
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        for (RoleEnum role : roles){
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getValue()));
        }
        return authorities;
    }
}
