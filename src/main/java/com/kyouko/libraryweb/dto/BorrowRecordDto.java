package com.kyouko.libraryweb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 借阅记录数据传输对象 (Borrow Record Data Transfer Object)
 * 
 * 用于在不同层之间传递借阅记录信息的数据载体。
 * 整合了借阅记录、图书信息、用户信息的完整数据。
 * 
 * 主要用途：
 * - 借阅记录查询和展示
 * - 管理员借阅管理界面
 * - 用户借阅历史查看
 * - API接口的响应数据格式
 * 
 * 数据整合特点：
 * - 包含借阅记录的核心信息
 * - 集成图书的基本信息（标题、ISBN）
 * - 包含用户的显示信息（用户名、昵称）
 * - 计算得出的业务字段（到期时间、剩余续借次数）
 * 
 * 时间字段说明：
 * - 所有时间字段使用统一的JSON格式化
 * - 支持中文本地化和东八区时区
 * - 精确到秒的时间显示
 * 
 * 设计优势：
 * - 减少前端的数据关联工作
 * - 提供完整的借阅信息视图
 * - 便于报表和统计功能
 * 
 * @author kyouko
 * @version 1.0
 */
@Data  // Lombok注解，自动生成getter、setter、toString、equals、hashCode方法
public class BorrowRecordDto {
    
    /**
     * 图书ISBN号
     * 
     * 国际标准书号，用于唯一标识图书。
     * 从借阅记录中的冗余字段获取，提高查询性能。
     * 
     * 用途：
     * - 快速识别借阅的图书
     * - 支持按ISBN搜索借阅记录
     * - 避免关联查询图书表
     */
    private String isbn;
    
    /**
     * 图书标题
     * 
     * 被借阅图书的完整标题名称。
     * 从图书信息表关联查询获得。
     * 
     * 用途：
     * - 在借阅记录列表中显示图书名称
     * - 用户快速识别借阅的图书
     * - 提升用户界面的可读性
     */
    private String title;
    
    /**
     * 用户名 (登录名)
     * 
     * 借阅用户的登录用户名。
     * 从借阅记录中的冗余字段获取。
     * 
     * 特点：
     * - 系统唯一标识
     * - 用于管理员识别用户
     * - 支持按用户名搜索借阅记录
     */
    private String username;
    
    /**
     * 用户昵称 (显示名称)
     * 
     * 借阅用户的友好显示名称。
     * 从用户信息表关联查询获得。
     * 
     * 用途：
     * - 提升界面显示的友好性
     * - 更好的用户体验
     * - 便于用户识别
     */
    private String nickName;
    
    /**
     * 借阅时间
     * 
     * 用户借阅图书的具体时间。
     * 使用JsonFormat注解统一前端显示格式。
     * 
     * 格式说明：
     * - locale="zh": 中文本地化
     * - timezone="GMT+8": 东八区时区
     * - pattern="yyyy-MM-dd HH:mm:ss": 年-月-日 时:分:秒
     * 
     * 业务意义：
     * - 借阅期限的起始时间
     * - 用于计算到期时间
     * - 借阅历史记录
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime borrowDate;
    
    /**
     * 归还时间
     * 
     * 用户归还图书的具体时间。
     * 如果图书尚未归还，此字段为null。
     * 
     * 状态判断：
     * - null: 图书尚未归还（借出状态）
     * - 有值: 图书已归还（归还状态）
     * 
     * 用途：
     * - 确定借阅状态
     * - 计算实际借阅时长
     * - 借阅历史统计
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnDate;
    
    /**
     * 到期时间 (应还时间)
     * 
     * 图书应该归还的截止时间。
     * 通常为借阅时间 + 30天（系统默认借阅期限）。
     * 
     * 计算规则：
     * - deadDate = borrowDate + 30天
     * - 续借会重新计算到期时间
     * 
     * 业务用途：
     * - 判断是否逾期
     * - 逾期提醒功能
     * - 借阅期限管理
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadDate;
    
    /**
     * 借阅状态
     * 
     * 当前借阅记录的状态标识。
     * 可能的值：
     * - "LEND": 已借出（图书在用户手中）
     * - "RETURN": 已归还（图书已还回）
     * 
     * 状态转换：
     * - 借阅时：设置为 LEND
     * - 归还时：更新为 RETURN
     * - 续借时：保持 LEND 状态
     */
    private String status;
    
    /**
     * 剩余续借次数
     * 
     * 用户对该图书还可以续借的次数。
     * 计算公式：剩余次数 = 最大续借次数 - 已续借次数
     * 
     * 业务规则：
     * - 系统设定最大续借次数（如3次）
     * - 每次续借后减少1次
     * - 为0时不能再续借
     * 
     * 用途：
     * - 控制续借权限
     * - 前端显示续借按钮状态
     * - 借阅管理
     */
    private Integer renewTimes;
    
    /**
     * 用户ID
     * 
     * 借阅用户的唯一标识。
     * 用于关联用户信息和执行用户相关操作。
     * 
     * 用途：
     * - 数据关联的外键
     * - 用户操作的参数
     * - 权限验证
     */
    private Long userId;
    
    /**
     * 图书ID
     * 
     * 被借阅图书的唯一标识。
     * 用于关联图书信息和执行图书相关操作。
     * 
     * 用途：
     * - 数据关联的外键
     * - 图书操作的参数
     * - 库存管理
     */
    private Long bookId;
}
