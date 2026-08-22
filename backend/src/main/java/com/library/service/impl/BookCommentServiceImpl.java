package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.entity.BookComment;
import com.library.mapper.BookCommentMapper;
import com.library.service.BookCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 图书评论服务实现
 *
 * @author library
 */
@Service
@RequiredArgsConstructor
public class BookCommentServiceImpl extends ServiceImpl<BookCommentMapper, BookComment> implements BookCommentService {

    private final BookCommentMapper bookCommentMapper;

    @Override
    public IPage<BookComment> pageList(Long page, Long size, String keyword, Integer status) {
        Page<BookComment> p = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
        return bookCommentMapper.selectPageWithBook(p, keyword, status);
    }

    @Override
    public boolean audit(Long id, Integer status) {
        BookComment comment = baseMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (status == null || (status != 1 && status != 2)) {
            throw new BusinessException("审核状态非法, 仅支持 1=通过 / 2=驳回");
        }
        LambdaUpdateWrapper<BookComment> wrapper = new LambdaUpdateWrapper<BookComment>()
                .eq(BookComment::getId, id)
                .set(BookComment::getStatus, status);
        return baseMapper.update(null, wrapper) > 0;
    }
}
