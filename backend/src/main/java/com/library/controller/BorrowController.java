package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.dto.BorrowDTO;
import com.library.entity.Borrow;
import com.library.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * 借阅控制器
 *
 * @author library
 */
@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    /**
     * 分页查询借阅记录(带图书名 + 读者名)
     */
    @GetMapping("/page")
    public Result<PageResult<Borrow>> page(@RequestParam(defaultValue = "1") Long page,
                                          @RequestParam(defaultValue = "10") Long size,
                                          @RequestParam(required = false) Integer status) {
        IPage<Borrow> result = borrowService.pageList(page, size, status);
        return Result.ok(PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize()));
    }

    /**
     * 借书
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('admin','librarian','reader')")
    public Result<Void> borrow(@RequestBody @Valid BorrowDTO dto) {
        borrowService.borrow(dto);
        return Result.ok();
    }

    /**
     * 还书
     */
    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('admin','librarian')")
    public Result<Void> doReturn(@PathVariable Long id) {
        borrowService.doReturn(id);
        return Result.ok();
    }

    /**
     * 逾期列表
     */
    @GetMapping("/overdue")
    public Result<List<Borrow>> overdue() {
        return Result.ok(borrowService.overdueList());
    }
}
