package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 借阅记录实体
 *
 * @author library
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("borrow")
public class Borrow extends BaseEntity {

    private Long bookId;

    private Long readerId;

    private LocalDate borrowDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    /**
     * 状态: 1借出中 2已归还 3已逾期
     */
    private Integer status;

    /**
     * 图书名称(非数据库字段, 用于联查返回)
     */
    @TableField(exist = false)
    private String bookTitle;

    /**
     * 读者姓名(非数据库字段, 用于联查返回)
     */
    @TableField(exist = false)
    private String readerName;
}
