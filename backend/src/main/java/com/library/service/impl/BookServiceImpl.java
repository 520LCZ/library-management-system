package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 图书服务实现
 *
 * @author library
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    private final BookMapper bookMapper;

    @Override
    public IPage<Book> pageList(Long page, Long size, String keyword, Long categoryId) {
        Page<Book> p = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
        return bookMapper.selectPageWithCategory(p, keyword, categoryId);
    }

    @Override
    public Book detail(Long id) {
        Book book = bookMapper.selectByIdWithCategory(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        return book;
    }

    @Override
    public boolean changeStatus(Long id, Integer status) {
        Book book = baseMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        LambdaUpdateWrapper<Book> wrapper = new LambdaUpdateWrapper<Book>()
                .eq(Book::getId, id)
                .set(Book::getStatus, status);
        return baseMapper.update(null, wrapper) > 0;
    }
}
