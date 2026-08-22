package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.service.BookService;
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
 * 图书控制器
 *
 * @author library
 */
@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 分页查询图书
     */
    @GetMapping("/page")
    public Result<PageResult<Book>> page(@RequestParam(defaultValue = "1") Long page,
                                        @RequestParam(defaultValue = "10") Long size,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Long categoryId) {
        IPage<Book> result = bookService.pageList(page, size, keyword, categoryId);
        return Result.ok(PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize()));
    }

    /**
     * 查询单本图书
     */
    @GetMapping("/{id}")
    public Result<Book> detail(@PathVariable Long id) {
        return Result.ok(bookService.detail(id));
    }

    /**
     * 新增图书
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('admin','librarian')")
    public Result<Void> add(@RequestBody Book book) {
        bookService.save(book);
        return Result.ok();
    }

    /**
     * 修改图书
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('admin','librarian')")
    public Result<Void> update(@RequestBody Book book) {
        bookService.updateById(book);
        return Result.ok();
    }

    /**
     * 删除图书
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.removeById(id);
        return Result.ok();
    }

    /**
     * 上下架
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('admin','librarian')")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        bookService.changeStatus(id, status);
        return Result.ok();
    }
}
