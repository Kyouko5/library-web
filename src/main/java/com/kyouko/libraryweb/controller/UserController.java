package com.kyouko.libraryweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kyouko.libraryweb.common.PlainResult;
import com.kyouko.libraryweb.dto.UserInfoDto;
import com.kyouko.libraryweb.dto.UserPasswordChangeDto;
import com.kyouko.libraryweb.service.UserService;
import com.kyouko.libraryweb.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 
 * 负责处理用户信息管理相关的请求。
 * 提供用户信息查询、更新、删除等功能的RESTful API接口。
 * 
 * 主要功能：
 * - 获取用户信息：通过JWT令牌获取当前用户信息
 * - 搜索用户：分页搜索用户，支持多条件筛选
 * - 更新用户信息：修改用户的基本信息
 * - 删除用户：单个删除或批量删除用户
 * - 修改密码：用户密码变更功能
 * 
 * 权限设计：
 * - 获取用户信息：需要有效JWT令牌
 * - 搜索用户：通常需要管理员权限
 * - 修改/删除用户：需要管理员权限或用户本人权限
 * - 修改密码：需要验证原密码
 * 
 * 安全考虑：
 * - 使用JWT令牌进行身份验证
 * - 敏感操作需要权限验证
 * - 密码修改需要原密码验证
 * - 不直接暴露用户敏感信息
 * 
 * @author kyouko
 * @version 1.0
 */
@RestController  // 标识为REST控制器，自动将返回值序列化为JSON
@RequestMapping("/api/users")  // 统一的API路径前缀
public class UserController {
    
    /**
     * JWT工具类依赖注入
     * 用于解析JWT令牌获取用户信息
     */
    @Resource
    private JwtUtil jwtUtil;
    
    /**
     * 用户服务层依赖注入
     * 处理用户相关的业务逻辑
     */
    @Resource
    private UserService userService;

    /**
     * 获取用户信息接口
     * 
     * 通过JWT令牌解析出用户名，然后查询用户的详细信息。
     * 主要用于前端获取当前登录用户的信息。
     * 
     * @param token JWT令牌，从前端请求参数中获取
     * @return PlainResult<UserInfoDto> 包含用户信息的统一响应格式
     * 
     * API路径: GET /api/users
     * 请求示例: GET /api/users?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     * 
     * 响应体示例:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "id": 1,
     *     "username": "testuser",
     *     "role": "READER",
     *     "nickName": "测试用户",
     *     "address": "北京市朝阳区",
     *     "phone": "13812345678",
     *     "email": "test@example.com"
     *   }
     * }
     * 
     * 注意事项：
     * - 令牌必须有效且未过期
     * - 返回的用户信息不包含密码等敏感信息
     * - 用于前端用户信息展示和权限判断
     */
    @GetMapping
    public PlainResult<UserInfoDto> getUserInfo(@RequestParam String token) {
        // 从JWT令牌中解析出用户名
        String username = jwtUtil.extractUsername(token);
        
        // 根据用户名查询用户详细信息
        UserInfoDto userInfoDto = userService.getUserByUsername(username);
        
        return PlainResult.success(userInfoDto);
    }

    /**
     * 搜索用户接口
     * 
     * 分页搜索用户信息，支持多条件组合搜索。
     * 主要用于管理员查看和管理系统中的所有用户。
     * 
     * @param pageNum 页码，从1开始（可选，默认为1）
     * @param pageSize 每页大小（可选，默认为10）
     * @param name 用户名或昵称搜索关键词（可选，支持模糊搜索）
     * @param phone 电话号码搜索关键词（可选，支持模糊搜索）
     * @param email 邮箱地址搜索关键词（可选，支持模糊搜索）
     * @return PlainResult<IPage<UserInfoDto>> 包含分页用户数据的统一响应格式
     * 
     * API路径: GET /api/users/search
     * 请求示例:
     * - GET /api/users/search  查询所有用户（使用默认分页）
     * - GET /api/users/search?pageNum=1&pageSize=20  指定分页参数
     * - GET /api/users/search?name=张三  按姓名搜索
     * - GET /api/users/search?phone=138  按电话搜索
     * - GET /api/users/search?email=gmail  按邮箱搜索
     * - GET /api/users/search?name=张&phone=138  组合搜索
     * 
     * 权限要求：
     * - 通常需要管理员权限
     * - 用于用户管理功能
     * 
     * 搜索特性：
     * - 支持多字段模糊搜索
     * - 分页返回结果
     * - 搜索条件可以任意组合
     */
    @GetMapping("/search")
    public PlainResult<IPage<UserInfoDto>> searchUser(@RequestParam(required = false) Integer pageNum,
                                                      @RequestParam(required = false) Integer pageSize,
                                                      @RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String phone,
                                                      @RequestParam(required = false) String email) {
        // 调用用户服务进行分页搜索
        IPage<UserInfoDto> userInfoDtoIPage = userService.searchUser(pageNum, pageSize, name, phone, email);
        // 返回分页查询接口的响应体
        return PlainResult.success(userInfoDtoIPage);
    }

    /**
     * 更新用户信息接口
     * 
     * 根据用户ID更新用户的基本信息。
     * 支持部分字段更新，不会影响密码等敏感信息。
     * 
     * @param id 要更新的用户ID
     * @param userInfoDto 包含更新信息的用户数据传输对象
     * @return PlainResult<String> 统一响应格式，成功时返回成功消息
     * 
     * API路径: PUT /api/users/{id}
     * 路径示例: PUT /api/users/1
     * 
     * 请求体示例:
     * {
     *   "nickName": "新昵称",
     *   "address": "新地址",
     *   "phone": "13987654321",
     *   "email": "newemail@example.com"
     * }
     * 
     * 权限控制：
     * - 管理员可以修改任何用户信息
     * - 普通用户只能修改自己的信息
     * - 用户名和角色等关键字段通常不允许修改
     * 
     * 业务规则：
     * - 电话号码和邮箱可能需要唯一性验证
     * - 某些字段可能有格式验证要求
     */
    @PutMapping("/{id}")
    public PlainResult<String> updateUser(@PathVariable Long id, @RequestBody UserInfoDto userInfoDto) {
        // 调用用户服务更新用户信息
        userService.updateUser(id, userInfoDto);
        return PlainResult.success("success");
    }
    
    /**
     * 删除用户接口
     * 
     * 根据用户ID删除指定用户。
     * 删除前会检查用户是否有未归还的图书。
     * 
     * @param id 要删除的用户ID
     * @return PlainResult<String> 统一响应格式，成功时返回成功消息
     * 
     * API路径: DELETE /api/users/{id}
     * 路径示例: DELETE /api/users/1
     * 
     * 权限要求：
     * - 通常需要管理员权限
     * - 不能删除自己的账户（防止系统无管理员）
     * 
     * 业务约束：
     * - 如果用户有未归还图书，不允许删除
     * - 删除操作不可逆，需要谨慎处理
     * - 可能需要级联处理相关数据
     */
    @DeleteMapping("/{id}")
    public PlainResult<String> deleteUser(@PathVariable Long id) {
        // 调用用户服务删除指定用户
        userService.deleteUser(id);
        return PlainResult.success("success");
    }

    /**
     * 批量删除用户接口
     * 
     * 根据用户ID列表批量删除多个用户。
     * 适用于管理员批量管理用户的场景。
     * 
     * @param ids 要删除的用户ID列表
     * @return PlainResult<String> 统一响应格式，成功时返回成功消息
     * 
     * API路径: POST /api/users/deleteBatch
     * 请求体示例: [1, 2, 3, 4, 5]
     * 
     * 权限要求：
     * - 需要管理员权限
     * - 批量操作需要特别谨慎
     * 
     * 业务处理：
     * - 会逐个检查每个用户的删除条件
     * - 如果某个用户不能删除，可能会跳过或回滚整个操作
     * - 操作结果可能需要详细的反馈信息
     */
    @PostMapping("/deleteBatch")
    public PlainResult<String> deleteBatch(@RequestBody List<Long> ids) {
        // 调用用户服务批量删除用户
        userService.batchDelete(ids);
        return PlainResult.success("success");
    }

    /**
     * 修改用户密码接口
     * 
     * 允许用户修改自己的登录密码。
     * 需要验证原密码的正确性，确保账户安全。
     * 
     * @param userPasswordChangeDto 密码修改请求数据传输对象，包含用户ID、原密码、新密码
     * @return PlainResult<String> 统一响应格式，成功时返回成功消息
     * 
     * API路径: POST /api/users/password
     * 
     * 请求体示例:
     * {
     *   "userId": 1,
     *   "oldPassword": "oldpassword123",
     *   "newPassword": "newpassword456"
     * }
     * 
     * 安全机制：
     * - 必须验证原密码的正确性
     * - 新密码需要满足强度要求
     * - 密码使用BCrypt加密存储
     * - 修改成功后可能需要重新登录
     * 
     * 业务规则：
     * - 新密码不能与原密码相同
     * - 密码长度和复杂度验证
     * - 可能有密码修改频率限制
     * 
     * 错误处理：
     * - 原密码错误时返回相应错误信息
     * - 新密码格式不合规时返回验证失败信息
     */
    @PostMapping("/password")
    public PlainResult<String> updateUserPassword(@RequestBody UserPasswordChangeDto userPasswordChangeDto) {
        // 调用用户服务更新用户密码
        userService.updateUserPassword(userPasswordChangeDto);
        return PlainResult.success("success");
    }
}
