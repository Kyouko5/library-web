package com.kyouko.libraryweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kyouko.libraryweb.common.LibraryException;
import com.kyouko.libraryweb.domain.User;
import com.kyouko.libraryweb.dto.*;
import com.kyouko.libraryweb.mapper.UserMapper;
import com.kyouko.libraryweb.service.UserService;
import com.kyouko.libraryweb.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 用户服务实现类
 * 
 * 实现UserService接口，提供用户管理的完整业务逻辑。
 * 
 * 主要功能：
 * - 用户注册和登录认证
 * - 用户信息的CRUD操作（创建、查询、更新、删除）
 * - 密码管理和安全验证
 * - 用户搜索和分页查询
 * - 数据传输对象(DTO)与实体对象的转换
 * 
 * 安全特性：
 * - 密码使用BCrypt加密存储，不存储明文密码
 * - 集成Spring Security进行身份认证
 * - JWT令牌生成和管理
 * - 用户名唯一性校验
 * - 密码修改时的安全验证
 * 
 * 技术实现：
 * - 使用MyBatis Plus进行数据库操作
 * - Spring Security AuthenticationManager进行认证
 * - 参数验证和业务异常处理
 * - 支持多条件组合查询
 * 
 * @author kyouko
 * @version 1.0
 */
@Service  // 标识为Spring服务层组件
public class UserServiceImpl implements UserService {

    /**
     * 用户数据访问层依赖注入
     * 用于执行用户相关的数据库操作
     */
    @Resource
    private UserMapper userMapper;

    /**
     * 密码编码器依赖注入
     * 使用BCrypt算法对密码进行加密和验证
     */
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * Spring Security认证管理器
     * 用于处理用户登录认证
     */
    @Resource
    private AuthenticationManager authenticationManager;

    /**
     * JWT工具类依赖注入
     * 用于生成和解析JWT令牌
     */
    @Resource
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     * 
     * 创建新用户账户，包含完整的用户信息验证和存储。
     * 
     * @param registerDto 用户注册信息DTO，包含用户名、密码、角色等信息
     * @throws LibraryException 当用户名已存在时抛出异常
     * 
     * 业务流程：
     * 1. 检查用户名是否已存在（保证唯一性）
     * 2. 创建新用户实体对象
     * 3. 对密码进行BCrypt加密
     * 4. 设置用户基本信息（角色、邮箱、电话等）
     * 5. 保存到数据库
     * 
     * 安全考虑：
     * - 密码使用BCrypt加密，不存储明文
     * - 用户名唯一性验证防止重复注册
     * - 支持角色分配（管理员/读者）
     */
    @Override
    public void register(RegisterDto registerDto) {
        // 检查用户名是否已存在
        User originUser = userMapper.findOneByUsername(registerDto.getUsername());
        if (originUser != null) {
            throw new LibraryException(400, "用户名已存在");
        }
        
        // 创建新用户对象
        User user = new User();
        user.setUsername(registerDto.getUsername());
        // 使用BCrypt对密码进行加密
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(registerDto.getRole());
        user.setEmail(registerDto.getEmail());
        user.setPhone(registerDto.getPhone());
        user.setAddress(registerDto.getAddress());
        user.setNickName(registerDto.getNickName());
        
        // 保存到数据库
        userMapper.insert(user);
    }

    /**
     * 用户登录
     * 
     * 验证用户凭据并生成JWT令牌用于后续认证。
     * 
     * @param loginRequestDto 登录请求DTO，包含用户名和密码
     * @return LoginResponseDto 登录响应DTO，包含JWT令牌
     * 
     * 认证流程：
     * 1. 创建Spring Security认证令牌
     * 2. 通过AuthenticationManager进行认证
     * 3. 认证成功后设置安全上下文
     * 4. 生成JWT令牌并返回
     * 
     * 安全机制：
     * - 使用Spring Security的认证机制
     * - 密码验证通过BCrypt进行
     * - 认证成功后生成JWT令牌
     * - 设置SecurityContext便于后续请求验证
     */
    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // 创建认证令牌
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(), 
                        loginRequestDto.getPassword()));
        
        // 设置安全上下文
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        
        // 生成JWT令牌并返回
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setToken(jwtUtil.generateToken(loginRequestDto.getUsername()));
        return loginResponseDto;
    }

    /**
     * 根据用户名查询用户信息
     * 
     * 通过用户名获取用户的详细信息，用于认证和授权。
     * 
     * @param username 用户名
     * @return UserInfoDto 用户信息DTO，如果用户不存在则返回null
     * 
     * 应用场景：
     * - JWT令牌验证时获取用户信息
     * - 用户个人信息展示
     * - 权限验证时获取用户角色
     */
    @Override
    public UserInfoDto getUserByUsername(String username) {
        User user = userMapper.findOneByUsername(username);
        if (user == null) {
            return null;
        }
        return toUserInfoDto(user);
    }

    /**
     * 分页搜索用户
     * 
     * 支持按昵称、电话、邮箱进行模糊搜索的分页查询。
     * 主要供管理员使用，用于用户管理功能。
     * 
     * @param pageNum 页码（从1开始），null或小于1时默认为1
     * @param pageSize 每页大小，null或小于1时默认为10
     * @param name 昵称搜索关键词（可选，支持模糊搜索）
     * @param phone 电话号码搜索关键词（可选，支持模糊搜索）
     * @param email 邮箱搜索关键词（可选，支持模糊搜索）
     * @return IPage<UserInfoDto> 分页结果，包含用户列表和分页信息
     * 
     * 查询特性：
     * - 支持多条件组合查询
     * - 按更新时间降序排列，最新更新的用户排在前面
     * - 使用Lambda表达式构建查询条件，类型安全
     * - 自动处理分页参数的边界情况
     * 
     * 性能优化：
     * - 使用MyBatis Plus的分页插件，在数据库层面进行分页
     * - 只查询当前页需要的数据，避免全表查询
     */
    @Override
    public IPage<UserInfoDto> searchUser(Integer pageNum, Integer pageSize, String name, String phone, String email) {
        // 参数校验和默认值设置
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        // 构建查询条件
        LambdaQueryWrapper<User> wrappers = Wrappers.lambdaQuery();
        wrappers.orderByDesc(User::getUpdatedAt);
        
        // 按昵称模糊搜索（如果提供了搜索关键词）
        if (StringUtils.hasText(name)) {
            wrappers.like(User::getNickName, name);
        }
        
        // 按电话号码模糊搜索（如果提供了搜索关键词）
        if (StringUtils.hasText(phone)) {
            wrappers.like(User::getPhone, phone);
        }
        
        // 按邮箱模糊搜索（如果提供了搜索关键词）
        if (StringUtils.hasText(email)) {
            wrappers.like(User::getEmail, email);
        }
        
        // 执行分页查询
        Page<User> userPage = userMapper.selectPage(new Page<>(pageNum, pageSize), wrappers);
        if (userPage == null) {
            return null;
        }
        
        // 将实体分页结果转换为DTO分页结果
        return userPage.convert(this::toUserInfoDto);
    }

    /**
     * 更新用户信息
     * 
     * 根据用户ID更新用户的基本信息（不包括密码）。
     * 
     * @param id 用户ID
     * @param userInfoDto 包含更新信息的用户DTO
     * 
     * 注意事项：
     * - 不更新密码，密码修改需要使用专门的方法
     * - 自动更新修改时间
     * - 不验证用户是否存在，如果ID不存在，更新操作不会生效
     */
    @Override
    public void updateUser(Long id, UserInfoDto userInfoDto) {
        User user = toUser(id, userInfoDto);
        userMapper.updateById(user);
    }

    /**
     * DTO转实体对象的私有方法
     * 
     * 将UserInfoDto转换为User实体对象，用于更新操作。
     * 
     * @param id 用户ID
     * @param userInfoDto 用户信息DTO
     * @return User 用户实体对象
     * 
     * 转换说明：
     * - 只转换可更新的字段，不包括密码
     * - 自动设置更新时间
     * - 保持原有的创建时间不变
     */
    private User toUser(Long id, UserInfoDto userInfoDto) {
        User user = new User();
        user.setId(id);
        user.setUsername(userInfoDto.getUsername());
        user.setNickName(userInfoDto.getNickName());
        user.setEmail(userInfoDto.getEmail());
        user.setPhone(userInfoDto.getPhone());
        user.setAddress(userInfoDto.getAddress());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * 删除用户
     * 
     * 根据用户ID删除对应的用户记录。
     * 
     * @param id 要删除的用户ID
     * 
     * 注意事项：
     * - 删除前应该检查是否有相关的借阅记录
     * - 当前实现为物理删除，可考虑改为逻辑删除（软删除）
     * - 如果ID不存在，删除操作不会报错
     */
    @Override
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    /**
     * 批量删除用户
     * 
     * 根据用户ID列表批量删除多个用户记录。
     * 适用于管理员的批量操作功能。
     * 
     * @param ids 要删除的用户ID列表
     * 
     * 性能优化：
     * - 使用MyBatis Plus的批量删除方法，比循环单个删除效率更高
     * - 在数据库层面执行批量删除，减少网络开销
     */
    @Override
    public void batchDelete(List<Long> ids) {
        userMapper.deleteBatchIds(ids);
    }

    /**
     * 修改用户密码
     * 
     * 验证旧密码后更新用户密码，包含完整的安全验证。
     * 
     * @param userPasswordChangeDto 密码修改DTO，包含用户ID、旧密码、新密码
     * @throws LibraryException 各种验证失败时抛出相应异常
     * 
     * 安全验证流程：
     * 1. 验证必填参数（用户ID、旧密码、新密码）
     * 2. 验证新密码与旧密码不能相同
     * 3. 验证用户是否存在
     * 4. 验证旧密码是否正确
     * 5. 加密新密码并更新到数据库
     * 
     * 安全特性：
     * - 必须提供正确的旧密码才能修改
     * - 新密码使用BCrypt加密存储
     * - 完整的参数验证和错误处理
     * - 防止密码设置为相同值
     */
    @Override
    public void updateUserPassword(UserPasswordChangeDto userPasswordChangeDto) {
        // 参数验证
        if (userPasswordChangeDto.getUserId() == null) {
            throw new LibraryException(400, "用户ID不能为空");
        }
        if (!StringUtils.hasText(userPasswordChangeDto.getOldPassword())) {
            throw new LibraryException(400, "旧密码不能为空");
        }
        if (!StringUtils.hasText(userPasswordChangeDto.getNewPassword())) {
            throw new LibraryException(400, "新密码不能为空");
        }
        
        // 验证新旧密码不能相同
        if (Objects.equals(userPasswordChangeDto.getOldPassword(), userPasswordChangeDto.getNewPassword())) {
            throw new LibraryException(400, "新密码不能与旧密码相同");
        }
        
        // 验证用户是否存在
        User user = userMapper.selectById(userPasswordChangeDto.getUserId());
        if (user == null) {
            throw new LibraryException(400, "用户不存在");
        }
        
        // 验证旧密码是否正确
        if (!passwordEncoder.matches(userPasswordChangeDto.getOldPassword(), user.getPassword())) {
            throw new LibraryException(500, "旧密码错误");
        }
        
        // 加密新密码并更新
        user.setPassword(passwordEncoder.encode(userPasswordChangeDto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 实体对象转DTO对象的私有方法
     * 
     * 将User实体对象转换为UserInfoDto数据传输对象。
     * 用于在服务层和控制层之间传递数据。
     * 
     * @param user 用户实体对象
     * @return UserInfoDto 用户信息DTO对象
     * 
     * 转换说明：
     * - 只转换业务相关的字段，不包括密码等敏感信息
     * - 不包含数据库相关的元数据（如创建时间、更新时间）
     * - 保护内部数据结构，避免直接暴露实体对象
     */
    private UserInfoDto toUserInfoDto(User user) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setNickName(user.getNickName());
        userInfoDto.setAddress(user.getAddress());
        userInfoDto.setPhone(user.getPhone());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setId(user.getId());
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setRole(user.getRole());
        return userInfoDto;
    }
}
