package com.template.application.service;

import com.template.application.dto.request.user.LoginUserReq;
import com.template.application.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 白给
 * @since 2024-07-04
 */
public interface UserService extends IService<User> {

    String login(LoginUserReq loginUserReq);

    /**
     * 获取基本信息
     *
     * @param id
     * @return
     */
    User getInfo(Integer id);
}
