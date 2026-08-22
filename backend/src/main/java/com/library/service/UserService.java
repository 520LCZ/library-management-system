package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.User;

/**
 * 系统用户服务接口
 *
 * @author library
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户
     */
    IPage<User> pageList(Long page, Long size, String keyword);

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);

    /**
     * 修改状态
     */
    boolean changeStatus(Long id, Integer status);

    /**
     * 保存用户(密码加密)
     */
    boolean createUser(User user);

    /**
     * 修改用户(若有密码则加密)
     */
    boolean updateUser(User user);
}
