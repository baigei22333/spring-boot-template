package com.template.application.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.template.application.dto.request.user.LoginUserReq;
import com.template.application.entity.User;
import com.template.application.mapper.UserMapper;
import com.template.application.service.UserService;
import com.template.application.utils.AesUtil;
import com.template.application.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 白给
 * @since 2024-07-04
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private AesUtil aesUtil;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public String login(LoginUserReq loginUserReq) {
        User user = baseMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, loginUserReq.getUserName()));

        if (ObjectUtil.isNull(user)) {
            throw new RuntimeException("该用户不存在");
        }

        if (!user.getPassword().equals(aesUtil.encrypt(loginUserReq.getPassword()))) {
            throw new RuntimeException("密码错误！");
        }
        return jwtUtil.generateToken(user.getId());
    }

    /**
     * 获取基本信息
     *
     * @param id
     * @return
     */
    @Override
    public User getInfo(Integer id) {
        User user = baseMapper.selectById(id);
        user.setPassword(null);
        return user;
    }
}
