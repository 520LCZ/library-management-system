package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统用户控制器(password 不返回)
 *
 * @author library
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public Result<PageResult<User>> page(@RequestParam(defaultValue = "1") Long page,
                                        @RequestParam(defaultValue = "10") Long size,
                                        @RequestParam(required = false) String keyword) {
        IPage<User> result = userService.pageList(page, size, keyword);
        // 清除密码后再返回
        List<User> records = result.getRecords();
        records.forEach(u -> u.setPassword(null));
        return Result.ok(PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize()));
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> add(@RequestBody User user) {
        userService.createUser(user);
        return Result.ok();
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        userService.updateUser(user);
        return Result.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.changeStatus(id, status);
        return Result.ok();
    }
}
