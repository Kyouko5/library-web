package com.kyouko.libraryweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体类
 * 
 * 对应数据库中的book表，用于存储图书的详细信息。
 * 包含图书的基本信息、库存管理和版本控制等功能。
 * 
 * 使用MyBatis Plus进行ORM映射
 * 使用Lombok的@Data注解自动生成getter/setter等方法
 * 
 * @author kyouko
 * @version 1.0
 */
@TableName("book")  // 指定对应的数据库表名
@Data  // Lombok注解，自动生成getter、setter、equals、hashCode、toString方法
public class Book {
    
    /**
     * 图书唯一标识ID
     * 使用数据库自增主键策略
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 图书标题/名称
     * 图书的主要标识信息，支持按标题搜索
     */
    @TableField("title")
    private String title;

    /**
     * 图书作者
     * 可以是单个作者或多个作者（用逗号分隔）
     */
    @TableField("author")
    private String author;

    /**
     * 出版社名称
     * 图书的出版机构信息
     */
    @TableField("publisher")
    private String publisher;

    /**
     * 国际标准书号(ISBN)
     * 图书的唯一标识码，支持ISBN搜索
     * 通常为10位或13位数字
     */
    @TableField("isbn")
    private String isbn;

    /**
     * 图书库存数量
     * 表示当前可借阅的图书数量
     * 借出时减少，归还时增加
     */
    @TableField("stock")
    private int stock;

    /**
     * 图书出版日期
     * 使用LocalDate类型存储年月日信息
     */
    @TableField("publish_time")
    private LocalDate publishTime;

    /**
     * 图书价格
     * 使用BigDecimal精确存储货币金额
     * 避免浮点数精度问题
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 数据版本号
     * 用于乐观锁控制，防止并发更新冲突
     * MyBatis Plus会自动处理版本号的更新
     */
    @TableField("version")
    private int version;

    /**
     * 记录创建时间
     * 使用MyBatis Plus的自动填充功能，在插入时自动设置
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 记录最后更新时间
     * 使用MyBatis Plus的自动填充功能，在插入和更新时自动设置
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}
