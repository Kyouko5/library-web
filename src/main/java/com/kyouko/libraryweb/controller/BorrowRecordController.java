package com.kyouko.libraryweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kyouko.libraryweb.common.PlainResult;
import com.kyouko.libraryweb.dto.BorrowRecordDto;
import com.kyouko.libraryweb.service.BorrowRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 借阅记录查询控制器
 * 
 * 负责处理借阅记录查询相关的请求。
 * 提供不同角度的借阅记录查询功能，支持管理员和用户的不同需求。
 * 
 * 主要功能：
 * - 全局借阅记录查询：管理员查看所有借阅记录
 * - 用户个人借阅记录查询：用户查看自己的借阅历史
 * 
 * 查询特性：
 * - 支持分页查询，避免数据量过大
 * - 支持多条件搜索（ISBN、用户名等）
 * - 返回整合的借阅信息（包含图书和用户信息）
 * 
 * 权限控制：
 * - 全局查询：需要管理员权限
 * - 个人查询：用户只能查看自己的记录
 * 
 * 数据整合：
 * - 返回的BorrowRecordDto包含完整的借阅信息
 * - 减少前端的数据关联工作
 * - 提供友好的显示格式
 * 
 * @author kyouko
 * @version 1.0
 */
@RestController  // 标识为REST控制器，自动将返回值序列化为JSON
@RequestMapping("/api/borrow")  // 统一的API路径前缀
public class BorrowRecordController {

    /**
     * 借阅记录服务层依赖注入
     * 处理借阅记录相关的业务逻辑
     */
    @Resource
    private BorrowRecordService borrowRecordService;

    /**
     * 搜索借阅记录接口（管理员视图）
     * 
     * 查询系统中的所有借阅记录，支持分页和条件搜索。
     * 主要用于管理员监控和管理整个系统的借阅情况。
     * 
     * @param pageNum 页码，从1开始（可选，默认为1）
     * @param pageSize 每页大小（可选，默认为10）
     * @param isbn 图书ISBN搜索条件（可选，支持精确搜索）
     * @param username 用户名搜索条件（可选，支持模糊搜索）
     * @return PlainResult<IPage<BorrowRecordDto>> 包含分页借阅记录的统一响应格式
     * 
     * API路径: GET /api/borrow/record
     * 请求示例:
     * - GET /api/borrow/record  查询所有借阅记录
     * - GET /api/borrow/record?pageNum=1&pageSize=20  指定分页
     * - GET /api/borrow/record?username=testuser  查询特定用户的借阅记录
     * - GET /api/borrow/record?isbn=9787111213826  查询特定图书的借阅记录
     * - GET /api/borrow/record?username=test&isbn=978  组合搜索
     * 
     * 权限要求：
     * - 需要管理员权限
     * - 可以查看所有用户的借阅记录
     * 
     * 返回数据特点：
     * - 包含借阅记录的完整信息
     * - 整合了图书标题、用户昵称等显示信息
     * - 包含借阅状态、到期时间、续借次数等业务信息
     * 
     * 使用场景：
     * - 管理员监控借阅情况
     * - 逾期图书管理
     * - 借阅统计和报表
     * - 用户借阅行为分析
     */
    @GetMapping("/record")
    public PlainResult<IPage<BorrowRecordDto>> searchRecord(@RequestParam(required = false) Integer pageNum,
                                                            @RequestParam(required = false) Integer pageSize,
                                                            @RequestParam(required = false) String isbn,
                                                            @RequestParam(required = false) String username) {
        // 调用借阅记录服务进行全局搜索（userId参数为null表示查询所有用户）
        IPage<BorrowRecordDto> borrowRecordDtoIPage = borrowRecordService.searchRecords(pageNum, pageSize, username, isbn, null);
        return PlainResult.success(borrowRecordDtoIPage);
    }

    /**
     * 查询用户个人借阅记录接口
     * 
     * 查询指定用户的个人借阅记录，支持分页和ISBN搜索。
     * 用户只能查看自己的借阅历史，管理员可以查看任意用户的记录。
     * 
     * @param pageNum 页码，从1开始（可选，默认为1）
     * @param pageSize 每页大小（可选，默认为10）
     * @param isbn 图书ISBN搜索条件（可选，用于在个人记录中搜索特定图书）
     * @param userId 用户ID（必需，指定要查询借阅记录的用户）
     * @return PlainResult<IPage<BorrowRecordDto>> 包含分页借阅记录的统一响应格式
     * 
     * API路径: GET /api/borrow/user/record
     * 请求示例:
     * - GET /api/borrow/user/record?userId=1  查询用户1的所有借阅记录
     * - GET /api/borrow/user/record?userId=1&pageNum=1&pageSize=10  指定分页
     * - GET /api/borrow/user/record?userId=1&isbn=9787111213826  搜索用户借阅的特定图书
     * 
     * 权限控制：
     * - 普通用户：只能查询自己的借阅记录（userId必须是自己的ID）
     * - 管理员：可以查询任意用户的借阅记录
     * 
     * 与全局查询的区别：
     * - 限定了特定用户的范围
     * - 不需要username参数（因为已通过userId限定）
     * - 更适合用户个人中心的借阅历史功能
     * 
     * 使用场景：
     * - 用户查看个人借阅历史
     * - 用户查找特定借阅记录
     * - 管理员查看特定用户的借阅情况
     * - 用户借阅行为分析
     */
    @GetMapping("/user/record")
    public PlainResult<IPage<BorrowRecordDto>> searchUserRecord(@RequestParam(required = false) Integer pageNum,
                                                                @RequestParam(required = false) Integer pageSize,
                                                                @RequestParam(required = false) String isbn,
                                                                @RequestParam(required = true) Long userId) {
        // 调用借阅记录服务进行用户特定搜索（username参数为null，通过userId限定用户）
        IPage<BorrowRecordDto> borrowRecordDtoIPage = borrowRecordService.searchRecords(pageNum, pageSize, null, isbn, userId);
        return PlainResult.success(borrowRecordDtoIPage);
    }
}
