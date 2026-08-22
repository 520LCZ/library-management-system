package com.library.controller;

import com.library.common.Result;
import com.library.dto.LoginDTO;
import com.library.dto.LoginVO;
import com.library.dto.UserInfoVO;
import com.library.entity.User;
import com.library.security.JwtUtil;
import com.library.security.SecurityUser;
import com.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author library
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(securityUser.getId(), securityUser.getUsername(), securityUser.getRole());
        UserInfoVO info = toUserInfo(securityUser);
        return Result.ok(new LoginVO(token, info));
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoVO> info() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
            return Result.fail(com.library.common.ResultCode.UNAUTHORIZED);
        }
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return Result.ok(toUserInfo(securityUser));
    }

    /**
     * 退出登录(无状态, 客户端丢弃 token 即可)
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.ok();
    }

    private UserInfoVO toUserInfo(SecurityUser su) {
        User user = userService.findByUsername(su.getUsername());
        UserInfoVO vo = new UserInfoVO();
        vo.setId(su.getId());
        vo.setUsername(su.getUsername());
        vo.setNickname(su.getNickname());
        vo.setRole(su.getRole());
        vo.setAvatar(su.getAvatar());
        if (user != null) {
            vo.setEmail(user.getEmail());
            vo.setPhone(user.getPhone());
            vo.setStatus(user.getStatus());
        }
        return vo;
    }
}
