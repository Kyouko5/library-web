package com.kyouko.libraryweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 * 
 * 对应数据库中的borrow_record表，用于记录图书借阅的详细信息。
 * 追踪用户的借书、还书历史，支持续借功能。
 * 
 * 借阅状态说明：
 * - "borrowed": 已借出，未归还
 * - "returned": 已归还
 * - "overdue": 超期未还
 * 
 * 使用MyBatis Plus进行ORM映射
 * 使用Lombok的@Data注解自动生成getter/setter等方法
 * 
 * @author kyouko
 * @version 1.0
 */
@TableName("borrow_record")  // 指定对应的数据库表名
@Data  // Lombok注解，自动生成getter、setter、equals、hashCode、toString方法
public class BorrowRecord {
    
    /**
     * 借阅记录唯一标识ID
     * 使用数据库自增主键策略
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 借阅用户的ID
     * 外键关联到user表的id字段
     * 标识是哪个用户进行的借阅操作
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 被借阅图书的ID
     * 外键关联到book表的id字段
     * 标识借阅的是哪本图书
     */
    @TableField("book_id")
    private Long bookId;

    /**
     * 借阅开始时间
     * 记录用户借书的具体时间点
     * 用于计算借阅期限和是否超期
     */
    @TableField("borrow_date")
    private LocalDateTime borrowDate;

    /**
     * 计划归还时间
     * 根据借阅政策设定的应还书时间
     * 超过此时间即为超期
     */
    @TableField("return_date")
    private LocalDateTime returnDate;

    /**
     * 借阅状态
     * "borrowed" - 已借出，未归还
     * "returned" - 已归还
     * "overdue" - 超期未还
     * 
     * 用于查询当前借阅状态和统计
     */
    @TableField("status")
    private String status;

    /**
     * 图书ISBN号
     * 冗余存储，方便查询和报表统计
     * 避免每次都要关联查询book表
     */
    @TableField("isbn")
    private String isbn;

    /**
     * 借阅用户名
     * 冗余存储，方便查询和显示
     * 避免每次都要关联查询user表
     */
    @TableField("username")
    private String username;

    /**
     * 续借次数
     * 记录该借阅记录的续借次数
     * 可用于限制续借次数上限
     */
    @TableField("renew_times")
    private Integer renewTimes;

    /**
     * 记录创建时间
     * 使用MyBatis Plus的自动填充功能，在插入时自动设置
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 记录最后更新时间
     * 使用MyBatis Plus的自动填充功能，在插入和更新时自动设置
     * 在归还或续借时会更新此字段
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
