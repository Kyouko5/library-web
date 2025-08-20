package com.kyouko.libraryweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 图书馆管理系统主应用程序入口类
 * 
 * 这是一个基于Spring Boot的图书馆管理Web应用程序。
 * 该系统提供以下主要功能：
 * - 用户注册和登录（管理员/读者角色）
 * - 图书信息管理（增删改查）
 * - 图书借阅记录管理
 * - 用户权限控制
 * - 数据仪表盘统计
 * 
 * 系统架构：
 * - 后端：Spring Boot + MyBatis Plus + MySQL
 * - 前端：Vue 3 + Element Plus
 * - 安全：Spring Security + JWT
 * 
 * @author kyouko
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication  // Spring Boot自动配置注解，启用自动配置、组件扫描等功能
public class LibraryWebApplication {

    /**
     * 应用程序主入口点
     * 启动Spring Boot应用程序容器，初始化所有Bean和配置
     * 
     * @param args 命令行参数（可用于传递配置参数）
     */
    public static void main(String[] args) {
        // 启动Spring Boot应用程序
        SpringApplication.run(LibraryWebApplication.class, args);
    }

}
