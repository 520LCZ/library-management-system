package com.library.controller;

import com.library.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色字典控制器
 *
 * @author library
 */
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    /**
     * 角色字典列表
     */
    @GetMapping("/list")
    public Result<List<Map<String, String>>> list() {
        return Result.ok(List.of(
                role("admin", "管理员"),
                role("librarian", "图书管理员"),
                role("reader", "读者")
        ));
    }

    private Map<String, String> role(String value, String label) {
        Map<String, String> m = new HashMap<>(2);
        m.put("value", value);
        m.put("label", label);
        return m;
    }
}
