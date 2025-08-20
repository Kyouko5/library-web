package com.kyouko.libraryweb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书数据传输对象 (Book Data Transfer Object)
 * 
 * 用于在不同层之间传递图书信息的数据载体。
 * 
 * 主要用途：
 * - 控制层与服务层之间的数据传递
 * - API接口的请求和响应数据格式
 * - 前端与后端的数据交换
 * - 避免直接暴露数据库实体对象
 * 
 * 设计特点：
 * - 只包含业务相关的字段，不包含数据库元数据
 * - 使用Jackson注解控制JSON序列化格式
 * - 使用Lombok简化getter/setter代码
 * - 字段类型与前端需求匹配
 * 
 * 与实体对象的区别：
 * - 不包含创建时间、更新时间、版本号等数据库字段
 * - 专门为API接口设计的数据结构
 * - 可以根据业务需要灵活调整字段
 * 
 * @author kyouko
 * @version 1.0
 */
@Data  // Lombok注解，自动生成getter、setter、toString、equals、hashCode方法
public class BookDto {
    
    /**
     * 图书唯一标识ID
     * 
     * 数据库主键，用于唯一标识一本图书。
     * 新增图书时为null，系统自动生成。
     */
    private Long id;
    
    /**
     * 图书标题
     * 
     * 图书的完整标题名称。
     * 用于显示和搜索，支持中英文。
     */
    private String title;
    
    /**
     * 作者姓名
     * 
     * 图书作者的姓名，支持多个作者用逗号分隔。
     * 用于图书检索和分类展示。
     */
    private String author;
    
    /**
     * 出版社名称
     * 
     * 图书的出版社信息。
     * 用于图书管理和版权信息展示。
     */
    private String publisher;
    
    /**
     * 国际标准书号 (ISBN)
     * 
     * 图书的唯一标识码，通常为10位或13位数字。
     * 用于图书的精确检索和管理。
     * 理论上应该是全局唯一的。
     */
    private String isbn;
    
    /**
     * 出版日期
     * 
     * 图书的出版时间，精确到日期。
     * 使用JsonFormat注解统一前端显示格式为 yyyy-MM-dd。
     * 
     * 注解说明：
     * - locale="zh": 使用中文本地化
     * - timezone="GMT+8": 使用东八区时区
     * - pattern="yyyy-MM-dd": 日期格式为年-月-日
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private LocalDate publishTime;
    
    /**
     * 库存数量
     * 
     * 当前图书的可借阅数量。
     * - 大于0：可以借阅
     * - 等于0：库存不足，无法借阅
     * - 借阅时减1，归还时加1
     * 
     * 注意：这是实时库存，不是总库存数量。
     */
    private int stock;
    
    /**
     * 图书价格
     * 
     * 图书的市场价格，使用BigDecimal确保精度。
     * 主要用于统计和展示，不涉及实际交易。
     * 
     * 使用BigDecimal的原因：
     * - 避免浮点数精度问题
     * - 适合货币计算
     * - 支持高精度的小数运算
     */
    private BigDecimal price;
    
    /**
     * 图书状态
     * 
     * 图书的当前状态标识。
     * 可能的值：
     * - "available": 可借阅
     * - "unavailable": 不可借阅
     * - 其他业务定义的状态值
     * 
     * 注意：此字段可能与库存数量有关联关系。
     */
    private String status;
}
