package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 系统用户服务实现
 *
 * @author library
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public IPage<User> pageList(Long page, Long size, String keyword) {
        Page<User> p = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
        return userMapper.selectPageWithKeyword(p, keyword);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public boolean changeStatus(Long id, Integer status) {
        User user = baseMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        return baseMapper.updateById(user) > 0;
    }

    @Override
    public boolean createUser(User user) {
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            throw new BusinessException("用户名/密码不能为空");
        }
        User exist = findByUsername(user.getUsername());
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (!StringUtils.hasText(user.getRole())) {
            user.setRole("reader");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (!StringUtils.hasText(user.getAvatar())) {
            user.setAvatar("");
        }
        return baseMapper.insert(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        User origin = baseMapper.selectById(user.getId());
        if (origin == null) {
            throw new BusinessException("用户不存在");
        }
        // 用户名变更时校验唯一性
        if (StringUtils.hasText(user.getUsername()) && !Objects.equals(user.getUsername(), origin.getUsername())) {
            User exist = findByUsername(user.getUsername());
            if (exist != null) {
                throw new BusinessException("用户名已存在");
            }
        }
        // 密码非空则加密; 否则沿用原密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(origin.getPassword());
        }
        return baseMapper.updateById(user) > 0;
    }
}
