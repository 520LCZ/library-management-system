package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书实体
 *
 * @author library
 */
@Data
@EqualsAndHashCode(callSuper = true)
@com.baomidou.mybatisplus.annotation.TableName("book")
public class Book extends BaseEntity {

    private String title;

    private String author;

    private String isbn;

    private Long categoryId;

    private String publisher;

    private LocalDate publishDate;

    private BigDecimal price;

    private Integer stock;

    private Integer total;

    private String cover;

    private String location;

    private String description;

    private Integer status;

    /**
     * 分类名称(非数据库字段, 用于联查返回)
     */
    @TableField(exist = false)
    private String categoryName;
}
