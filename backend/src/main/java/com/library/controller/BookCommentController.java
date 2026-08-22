package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.BookComment;
import com.library.service.BookCommentService;
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

/**
 * 图书评论控制器
 *
 * @author library
 */
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class BookCommentController {

    private final BookCommentService bookCommentService;

    /**
     * 分页查询评论
     */
    @GetMapping("/page")
    public Result<PageResult<BookComment>> page(@RequestParam(defaultValue = "1") Long page,
                                                @RequestParam(defaultValue = "10") Long size,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Integer status) {
        IPage<BookComment> result = bookCommentService.pageList(page, size, keyword, status);
        return Result.ok(PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize()));
    }

    /**
     * 新增评论(读者提交, 强制 status=0 待审核)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('admin','librarian','reader')")
    public Result<Void> add(@RequestBody BookComment comment) {
        comment.setStatus(0);
        bookCommentService.save(comment);
        return Result.ok();
    }

    /**
     * 审核评论: status=1 通过 / 2 驳回
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('admin','librarian')")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Integer status) {
        bookCommentService.audit(id, status);
        return Result.ok();
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> delete(@PathVariable Long id) {
        bookCommentService.removeById(id);
        return Result.ok();
    }
}
