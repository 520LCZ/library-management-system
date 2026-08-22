package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Reader;
import com.library.service.ReaderService;
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
 * 读者控制器
 *
 * @author library
 */
@RestController
@RequestMapping("/api/reader")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    /**
     * 分页查询读者
     */
    @GetMapping("/page")
    public Result<PageResult<Reader>> page(@RequestParam(defaultValue = "1") Long page,
                                          @RequestParam(defaultValue = "10") Long size,
                                          @RequestParam(required = false) String keyword) {
        IPage<Reader> result = readerService.pageList(page, size, keyword);
        return Result.ok(PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize()));
    }

    /**
     * 读者详情(含借阅历史)
     */
    @GetMapping("/{id}")
    public Result<Reader> detail(@PathVariable Long id) {
        return Result.ok(readerService.detail(id));
    }

    /**
     * 新增读者
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('admin','librarian')")
    public Result<Void> add(@RequestBody Reader reader) {
        readerService.save(reader);
        return Result.ok();
    }

    /**
     * 修改读者
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('admin','librarian')")
    public Result<Void> update(@RequestBody Reader reader) {
        readerService.updateById(reader);
        return Result.ok();
    }

    /**
     * 删除读者
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin','librarian')")
    public Result<Void> delete(@PathVariable Long id) {
        readerService.removeById(id);
        return Result.ok();
    }
}
