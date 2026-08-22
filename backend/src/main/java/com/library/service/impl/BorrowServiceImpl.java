package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.dto.BorrowDTO;
import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowMapper;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 借阅服务实现
 *
 * @author library
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowServiceImpl extends ServiceImpl<BorrowMapper, Borrow> implements BorrowService {

    private final BorrowMapper borrowMapper;
    private final BookMapper bookMapper;

    @Override
    public IPage<Borrow> pageList(Long page, Long size, Integer status) {
        Page<Borrow> p = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
        return borrowMapper.selectPageWithDetail(p, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean borrow(BorrowDTO dto) {
        Book book = bookMapper.selectById(dto.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getStatus() != null && book.getStatus() == 0) {
            throw new BusinessException("该图书已下架, 不可借阅");
        }
        if (book.getStock() == null || book.getStock() <= 0) {
            throw new BusinessException("库存不足, 无法借阅");
        }
        // 扣减库存
        int rows = bookMapper.update(null,
                new LambdaUpdateWrapper<Book>()
                        .eq(Book::getId, book.getId())
                        .gt(Book::getStock, 0)
                        .setSql("stock = stock - 1"));
        if (rows <= 0) {
            throw new BusinessException("扣减库存失败, 可能已被借出");
        }
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(dto.getDays());
        Borrow borrow = new Borrow();
        borrow.setBookId(dto.getBookId());
        borrow.setReaderId(dto.getReaderId());
        borrow.setBorrowDate(today);
        borrow.setDueDate(dueDate);
        borrow.setStatus(1);
        return borrowMapper.insert(borrow) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doReturn(Long id) {
        Borrow borrow = borrowMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (borrow.getReturnDate() != null) {
            throw new BusinessException("该记录已归还");
        }
        LocalDate today = LocalDate.now();
        // 恢复库存
        int rows = bookMapper.update(null,
                new LambdaUpdateWrapper<Book>()
                        .eq(Book::getId, borrow.getBookId())
                        .setSql("stock = stock + 1"));
        if (rows <= 0) {
            log.warn("恢复库存时未更新到对应图书, bookId={}", borrow.getBookId());
        }
        // 更新归还记录
        borrowMapper.update(null,
                new LambdaUpdateWrapper<Borrow>()
                        .eq(Borrow::getId, id)
                        .set(Borrow::getReturnDate, today)
                        .set(Borrow::getStatus, 2));
        return true;
    }

    @Override
    public List<Borrow> overdueList() {
        // 同步状态后再返回
        refreshOverdueStatus();
        return borrowMapper.selectOverdueList();
    }

    @Override
    public void refreshOverdueStatus() {
        try {
            borrowMapper.updateOverdueStatus();
        } catch (Exception e) {
            log.warn("刷新逾期状态失败: {}", e.getMessage());
        }
    }
}
