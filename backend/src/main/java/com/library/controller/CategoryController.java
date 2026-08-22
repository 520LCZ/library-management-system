package com.library.controller;

import com.library.common.Result;
import com.library.entity.Category;
import com.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器
 *
 * @author library
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 分类树
     */
    @GetMapping("/tree")
    public Result<List<Category>> tree() {
        return Result.ok(categoryService.tree());
    }

    /**
     * 全部分类
     */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryService.list());
    }

    /**
     * 新增分类
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('admin','librarian')")
    public Result<Void> add(@RequestBody Category category) {
        categoryService.save(category);
        return Result.ok();
    }

    /**
     * 修改分类
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('admin','librarian')")
    public Result<Void> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.ok();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.ok();
    }
}
