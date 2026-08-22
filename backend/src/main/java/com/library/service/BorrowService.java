package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.BorrowDTO;
import com.library.entity.Borrow;

import java.util.List;

/**
 * 借阅服务接口
 *
 * @author library
 */
public interface BorrowService extends IService<Borrow> {

    /**
     * 分页查询借阅记录(带详情)
     */
    IPage<Borrow> pageList(Long page, Long size, Integer status);

    /**
     * 借书: 扣减库存, 生成借阅记录
     */
    boolean borrow(BorrowDTO dto);

    /**
     * 还书: 恢复库存, 标记归还
     */
    boolean doReturn(Long id);

    /**
     * 逾期列表
     */
    List<Borrow> overdueList();

    /**
     * 同步逾期状态
     */
    void refreshOverdueStatus();
}
