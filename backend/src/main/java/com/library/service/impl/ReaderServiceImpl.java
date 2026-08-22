package com.library.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.entity.Borrow;
import com.library.entity.Reader;
import com.library.mapper.BorrowMapper;
import com.library.mapper.ReaderMapper;
import com.library.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 读者服务实现
 *
 * @author library
 */
@Service
@RequiredArgsConstructor
public class ReaderServiceImpl extends ServiceImpl<ReaderMapper, Reader> implements ReaderService {

    private final ReaderMapper readerMapper;
    private final BorrowMapper borrowMapper;

    @Override
    public IPage<Reader> pageList(Long page, Long size, String keyword) {
        Page<Reader> p = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
        return readerMapper.selectPageWithKeyword(p, keyword);
    }

    @Override
    public Reader detail(Long id) {
        Reader reader = baseMapper.selectById(id);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        // 查询借阅历史
        List<Borrow> history = borrowMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Borrow>()
                        .eq(Borrow::getReaderId, id)
                        .orderByDesc(Borrow::getId));
        reader.setBorrowHistory(history);
        return reader;
    }
}
