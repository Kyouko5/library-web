package com.kyouko.libraryweb.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * 对应数据库中的user表，用于存储系统用户信息。
 * 系统支持两种角色：管理员（role=1）和读者（role=2）
 * 
 * 使用MyBatis Plus进行ORM映射
 * 使用Lombok的@Data注解自动生成getter/setter等方法
 * 
 * @author kyouko
 * @version 1.0
 */
@TableName("user")  // 指定对应的数据库表名
@Data  // Lombok注解，自动生成getter、setter、equals、hashCode、toString方法
public class User {
    
    /**
     * 用户唯一标识ID
     * 使用数据库自增主键策略
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户登录名
     * 必须唯一，用于登录验证
     */
    @TableField("username")
    private String username;

    /**
     * 用户密码
     * 存储时使用BCrypt加密，不以明文形式保存
     */
    @TableField("password")
    private String password;

    /**
     * 用户昵称/显示名称
     * 用于在界面上显示的友好名称
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 用户角色标识
     * role == "1" 表示管理员，拥有所有系统权限
     * role == "2" 表示读者，只能进行图书借阅相关操作
     * 
     * 角色控制了用户在系统中的权限范围
     */
    @TableField("role")
    private String role;

    /**
     * 用户邮箱地址
     * 可用于找回密码、接收通知等功能
     */
    @TableField("email")
    private String email;

    /**
     * 用户手机号码
     * 可用于联系用户、接收通知等
     */
    @TableField("phone")
    private String phone;

    /**
     * 用户联系地址
     * 存储用户的详细地址信息
     */
    @TableField("address")
    private String address;

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
