package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * 读者实体
 *
 * @author library
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reader")
public class Reader extends BaseEntity {

    private String name;

    private Integer gender;

    private String phone;

    private String email;

    private String idCard;

    private String address;

    private LocalDate registerDate;

    private Integer status;

    /**
     * 借阅历史(非数据库字段)
     */
    @TableField(exist = false)
    private List<Borrow> borrowHistory;
}
