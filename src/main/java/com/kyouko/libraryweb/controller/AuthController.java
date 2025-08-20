package com.kyouko.libraryweb.controller;

import com.kyouko.libraryweb.common.PlainResult;
import com.kyouko.libraryweb.dto.LoginRequestDto;
import com.kyouko.libraryweb.dto.LoginResponseDto;
import com.kyouko.libraryweb.dto.RegisterDto;
import com.kyouko.libraryweb.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户认证控制器
 * 
 * 负责处理用户的注册和登录相关请求。
 * 提供RESTful API接口供前端调用。
 * 
 * 主要功能：
 * - 用户注册：创建新的用户账户
 * - 用户登录：验证用户凭据并返回JWT令牌
 * 
 * 安全机制：
 * - 密码使用BCrypt加密存储
 * - 登录成功后返回JWT令牌用于后续请求认证
 * - 所有接口返回统一的响应格式
 * 
 * @author kyouko
 * @version 1.0
 */
@RestController  // 标识为REST控制器，自动将返回值序列化为JSON
@RequestMapping("/api/auth")  // 统一的API路径前缀
public class AuthController {

    /**
     * 用户服务层依赖注入
     * 处理用户相关的业务逻辑
     */
    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     * 
     * 接收用户注册信息，创建新的用户账户。
     * 会验证用户名的唯一性，密码会进行加密存储。
     * 
     * @param registerDto 注册请求数据传输对象，包含用户名、密码、邮箱等信息
     * @return PlainResult<String> 统一响应格式，成功时返回注册成功消息
     * 
     * @throws LibraryException 当用户名已存在时抛出异常
     * 
     * API路径: POST /api/auth/register
     * 请求体示例:
     * {
     *   "username": "testuser",
     *   "password": "password123",
     *   "email": "test@example.com",
     *   "nickName": "测试用户"
     * }
     */
    @PostMapping("/register")
    public PlainResult<String> register(@RequestBody RegisterDto registerDto) {
        // 调用用户服务进行注册业务处理
        userService.register(registerDto);
        
        // 返回成功响应
        return PlainResult.success("注册成功");
    }

    /**
     * 用户登录接口
     * 
     * 验证用户凭据（用户名和密码），成功后返回JWT令牌。
     * 前端需要保存令牌并在后续请求中携带用于身份验证。
     * 
     * @param loginRequestDto 登录请求数据传输对象，包含用户名和密码
     * @return PlainResult<LoginResponseDto> 统一响应格式，成功时返回包含JWT令牌和用户信息的响应
     * 
     * @throws LibraryException 当用户名不存在或密码错误时抛出异常
     * 
     * API路径: POST /api/auth/login
     * 请求体示例:
     * {
     *   "username": "testuser",
     *   "password": "password123"
     * }
     * 
     * 响应体示例:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *     "userInfo": {
     *       "id": 1,
     *       "username": "testuser",
     *       "role": "2"
     *     }
     *   }
     * }
     */
    @PostMapping("/login")
    public PlainResult<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        // 调用用户服务进行登录验证和JWT令牌生成
        LoginResponseDto loginResponseDto = userService.login(loginRequestDto);
        
        // 返回包含令牌和用户信息的成功响应
        return PlainResult.success(loginResponseDto);
    }
}
