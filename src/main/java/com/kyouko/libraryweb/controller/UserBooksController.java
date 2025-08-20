package com.kyouko.libraryweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kyouko.libraryweb.common.LibraryConstants;
import com.kyouko.libraryweb.common.PlainResult;
import com.kyouko.libraryweb.domain.BorrowRecord;
import com.kyouko.libraryweb.dto.BookBorrowRequestDto;
import com.kyouko.libraryweb.dto.BookDto;
import com.kyouko.libraryweb.dto.BookReturnRequestDto;
import com.kyouko.libraryweb.dto.UserBorrowBooksInfoDto;
import com.kyouko.libraryweb.service.BookBorrowService;
import com.kyouko.libraryweb.service.BookService;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户图书操作控制器
 * 
 * 负责处理用户端的图书相关操作请求。
 * 提供用户借书、还书、续借等核心功能的API接口。
 * 
 * 主要功能：
 * - 借书：用户借阅图书的核心功能
 * - 还书：用户归还已借图书
 * - 续借：延长图书借阅期限
 * - 查看借阅信息：获取用户当前借阅状态
 * - 浏览图书：用户视角的图书列表（标记借阅状态）
 * 
 * 与管理员图书控制器的区别：
 * - 面向普通用户，权限较低
 * - 专注于借阅相关的业务操作
 * - 返回数据包含用户个性化信息（如借阅状态）
 * - 不包含图书的增删改功能
 * 
 * 业务特点：
 * - 所有操作都需要用户身份验证
 * - 借阅操作有业务规则限制（库存、借阅数量等）
 * - 状态管理：实时更新图书和用户的借阅状态
 * - 数据整合：返回对用户友好的完整信息
 * 
 * @author kyouko
 * @version 1.0
 */
@RestController  // 标识为REST控制器，自动将返回值序列化为JSON
@RequestMapping("/api/user/books")  // 统一的API路径前缀，表明这是用户端的图书操作
public class UserBooksController {

    /**
     * 图书借阅服务层依赖注入
     * 处理借书、还书、续借等核心业务逻辑
     */
    @Resource
    private BookBorrowService bookBorrowService;

    /**
     * 图书服务层依赖注入
     * 处理图书查询相关的业务逻辑
     */
    @Resource
    private BookService bookService;

    /**
     * 借书接口
     * 
     * 用户借阅图书的核心功能。
     * 会检查库存、用户借阅限制等业务规则，并更新相关状态。
     * 
     * @param bookBorrowRequestDto 借书请求数据传输对象，包含用户ID和图书ID
     * @return PlainResult<Boolean> 统一响应格式，返回借书操作是否成功
     * 
     * API路径: POST /api/user/books/borrow
     * 
     * 请求体示例:
     * {
     *   "userId": 1,
     *   "bookId": 100
     * }
     * 
     * 业务规则验证：
     * - 图书库存必须大于0
     * - 用户不能重复借阅同一本书
     * - 用户借阅数量不能超过限制（如最多借5本）
     * - 用户没有逾期未还的图书
     * 
     * 操作结果：
     * - 成功：创建借阅记录，减少图书库存，返回true
     * - 失败：抛出业务异常，返回具体错误信息
     * 
     * 事务保证：
     * - 借阅操作在事务中执行，确保数据一致性
     * - 失败时自动回滚，不会产生数据不一致
     */
    @PostMapping("/borrow")
    public PlainResult<Boolean> borrowBook(@RequestBody BookBorrowRequestDto bookBorrowRequestDto) {
        // 调用图书借阅服务执行借书业务逻辑
        boolean ret = bookBorrowService.borrowBook(bookBorrowRequestDto);
        return PlainResult.success(ret);
    }

    /**
     * 续借图书接口
     * 
     * 用户对已借阅的图书申请延长借阅期限。
     * 续借会重新计算到期时间，并减少剩余续借次数。
     * 
     * @param bookBorrowRequestDto 续借请求数据传输对象，包含用户ID和图书ID
     * @return PlainResult<Boolean> 统一响应格式，返回续借操作是否成功
     * 
     * API路径: POST /api/user/books/borrow/renew
     * 
     * 请求体示例:
     * {
     *   "userId": 1,
     *   "bookId": 100
     * }
     * 
     * 续借条件验证：
     * - 用户确实借阅了该图书且未归还
     * - 图书尚未逾期（或在宽限期内）
     * - 用户还有剩余续借次数（通常最多续借3次）
     * - 该图书没有其他用户预约等待
     * 
     * 续借效果：
     * - 延长借阅到期时间（通常延长30天）
     * - 减少用户对该书的剩余续借次数
     * - 更新借阅记录的相关时间信息
     * 
     * 业务价值：
     * - 提高用户体验，避免频繁借还
     * - 减少管理员工作量
     * - 提高图书利用效率
     */
    @PostMapping("/borrow/renew")
    public PlainResult<Boolean> renewBook(@RequestBody BookBorrowRequestDto bookBorrowRequestDto) {
        // 调用图书借阅服务执行续借业务逻辑
        boolean ret = bookBorrowService.renewBook(bookBorrowRequestDto);
        return PlainResult.success(ret);
    }

    /**
     * 还书接口
     * 
     * 用户归还已借阅的图书。
     * 会更新借阅记录状态并增加图书库存。
     * 
     * @param returnRequestDto 还书请求数据传输对象，包含用户ID和图书ID
     * @return PlainResult<Boolean> 统一响应格式，返回还书操作是否成功
     * 
     * API路径: POST /api/user/books/return
     * 
     * 请求体示例:
     * {
     *   "userId": 1,
     *   "bookId": 100
     * }
     * 
     * 还书验证：
     * - 用户确实借阅了该图书且尚未归还
     * - 借阅记录状态为"LEND"（已借出）
     * 
     * 还书处理：
     * - 更新借阅记录状态为"RETURN"（已归还）
     * - 设置实际归还时间
     * - 增加图书库存数量
     * - 如果逾期，可能记录逾期信息
     * 
     * 后续处理：
     * - 检查是否有其他用户等待借阅该书
     * - 更新用户的借阅统计信息
     * - 可能触发逾期费用计算（如果系统支持）
     */
    @PostMapping("/return")
    public PlainResult<Boolean> returnBook(@RequestBody BookReturnRequestDto returnRequestDto) {
        // 调用图书借阅服务执行还书业务逻辑
        boolean ret = bookBorrowService.returnBook(returnRequestDto);
        return PlainResult.success(ret);
    }

    /**
     * 获取用户借阅信息接口
     * 
     * 查询用户当前的借阅状态和统计信息。
     * 用于用户个人中心展示借阅概览。
     * 
     * @param userId 用户ID（必需参数）
     * @return PlainResult<UserBorrowBooksInfoDto> 包含用户借阅信息的统一响应格式
     * 
     * API路径: GET /api/user/books/info
     * 请求示例: GET /api/user/books/info?userId=1
     * 
     * 返回信息包含：
     * - 当前借阅的图书列表
     * - 借阅图书的到期时间
     * - 剩余续借次数
     * - 逾期图书信息
     * - 借阅历史统计
     * 
     * 响应体示例:
     * {
     *   "code": 200,
     *   "message": "success", 
     *   "data": {
     *     "currentBorrowCount": 3,      // 当前借阅数量
     *     "maxBorrowLimit": 5,          // 最大借阅限制
     *     "overdueCount": 1,            // 逾期图书数量
     *     "borrowedBooks": [            // 当前借阅的图书列表
     *       {
     *         "bookId": 100,
     *         "title": "Java编程思想",
     *         "borrowDate": "2024-01-01",
     *         "dueDate": "2024-01-31",
     *         "renewTimes": 2
     *       }
     *     ]
     *   }
     * }
     * 
     * 权限控制：
     * - 用户只能查看自己的借阅信息
     * - 管理员可以查看任意用户的借阅信息
     */
    @GetMapping("/info")
    public PlainResult<UserBorrowBooksInfoDto> borrowBooksInfo(@RequestParam(required = true) Long userId) {
        // 调用图书借阅服务获取用户借阅信息
        UserBorrowBooksInfoDto userBorrowBooksInfo = bookBorrowService.getUserBorrowBooksInfo(userId);
        return PlainResult.success(userBorrowBooksInfo);
    }

    /**
     * 获取图书列表接口（用户视角）
     * 
     * 查询图书列表，并标记用户的借阅状态。
     * 与管理员的图书列表不同，这个接口会显示用户是否已借阅某本书。
     * 
     * @param pageNum 页码，从1开始（可选，默认为1）
     * @param pageSize 每页大小（可选，默认为10）
     * @param title 图书标题搜索关键词（可选，支持模糊搜索）
     * @param isbn ISBN搜索关键词（可选，支持精确搜索）
     * @param userId 用户ID（必需，用于标记借阅状态）
     * @return PlainResult<IPage<BookDto>> 包含分页图书数据的统一响应格式
     * 
     * API路径: GET /api/user/books
     * 请求示例:
     * - GET /api/user/books?userId=1  查询所有图书并标记借阅状态
     * - GET /api/user/books?userId=1&title=Java  搜索包含"Java"的图书
     * - GET /api/user/books?userId=1&pageNum=2&pageSize=20  分页查询
     * 
     * 特殊处理逻辑：
     * 1. 首先调用基础的图书查询服务
     * 2. 查询用户当前借阅的图书列表
     * 3. 对比两个列表，标记用户已借阅的图书状态
     * 4. 返回带有借阅状态标记的图书列表
     * 
     * 状态标记说明：
     * - 如果用户已借阅某本书，该书的status会被设置为"LEND"
     * - 未借阅的图书保持原有状态（通常为"available"）
     * 
     * 前端使用：
     * - 可以根据status判断是否显示"借阅"或"已借阅"按钮
     * - 已借阅的图书可以显示不同的样式
     * - 支持用户快速了解自己的借阅状态
     * 
     * 性能优化：
     * - 只在有图书数据时才查询用户借阅记录
     * - 使用Stream API进行高效的数据处理
     * - 避免不必要的数据库查询
     */
    @GetMapping
    public PlainResult<IPage<BookDto>> getAllBooks(@RequestParam(required = false) Integer pageNum,
                                                   @RequestParam(required = false) Integer pageSize,
                                                   @RequestParam(required = false) String title,
                                                   @RequestParam(required = false) String isbn,
                                                   @RequestParam(required = true) Long userId) {
        // 1. 调用基础图书服务获取分页图书数据
        IPage<BookDto> books = bookService.getAllBooks(pageNum, pageSize, title, isbn);
        
        // 2. 如果查询到图书数据，则进行借阅状态标记处理
        if (!CollectionUtils.isEmpty(books.getRecords())) {
            // 提取当前页所有图书的ID列表
            List<Long> bookIds = books.getRecords().stream()
                    .map(BookDto::getId)
                    .collect(Collectors.toList());
            
            // 查询用户在这些图书中已借阅的记录
            List<BorrowRecord> borrowBooks = bookBorrowService.findBorrowBooks(bookIds, userId);
            
            // 3. 如果用户有借阅记录，则标记对应图书的状态
            if (!CollectionUtils.isEmpty(borrowBooks)) {
                // 提取用户已借阅的图书ID列表
                List<Long> borrowBookIds = borrowBooks.stream()
                        .map(BorrowRecord::getBookId)
                        .collect(Collectors.toList());
                
                // 遍历图书列表，标记已借阅的图书状态
                books.getRecords().forEach(book -> {
                    if (borrowBookIds.contains(book.getId())) {
                        // 设置状态为"LEND"，表示用户已借阅
                        book.setStatus(LibraryConstants.STATUS_LEND);
                    }
                    // 未借阅的图书保持原有状态不变
                });
            }
        }
        
        return PlainResult.success(books);
    }
}
