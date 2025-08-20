package com.kyouko.libraryweb.service.impl;

import com.kyouko.libraryweb.common.LibraryConstants;
import com.kyouko.libraryweb.common.LibraryException;
import com.kyouko.libraryweb.domain.BorrowRecord;
import com.kyouko.libraryweb.domain.User;
import com.kyouko.libraryweb.dto.*;
import com.kyouko.libraryweb.mapper.BorrowRecordMapper;
import com.kyouko.libraryweb.mapper.UserMapper;
import com.kyouko.libraryweb.service.BookBorrowService;
import com.kyouko.libraryweb.service.BookService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kyouko.libraryweb.common.LibraryConstants.STATUS_LEND;
import static com.kyouko.libraryweb.util.RoleUtil.isReader;

/**
 * 图书借阅服务实现类
 * 
 * 实现BookBorrowService接口，提供图书借阅管理的核心业务逻辑。
 * 
 * 主要功能：
 * - 图书借阅操作（借书、还书、续借）
 * - 借阅状态管理和验证
 * - 用户借阅信息查询和统计
 * - 逾期图书检查和提醒
 * - 借阅权限验证
 * 
 * 业务规则：
 * - 只有读者角色的用户才能借阅图书
 * - 同一用户不能重复借阅同一本书
 * - 图书库存必须大于0才能借阅
 * - 借阅期限为30天，支持续借
 * - 借阅和归还操作需要同时更新库存
 * 
 * 技术特性：
 * - 使用事务保证借阅操作的原子性
 * - 集成库存管理，自动处理库存增减
 * - 支持批量操作和复杂查询
 * - 提供逾期图书的自动检测
 * 
 * 数据一致性：
 * - 借阅记录与库存变更在同一事务中执行
 * - 冗余存储用户名和ISBN，提高查询性能
 * - 状态管理确保数据的准确性
 * 
 * @author kyouko
 * @version 1.0
 */
@Service  // 标识为Spring服务层组件
public class BookBorrowServiceImpl implements BookBorrowService {
    
    /**
     * 图书服务依赖注入
     * 用于获取图书信息和管理库存
     */
    @Resource
    private BookService bookService;
    
    /**
     * 借阅记录数据访问层依赖注入
     * 用于管理借阅记录的CRUD操作
     */
    @Resource
    private BorrowRecordMapper borrowRecordMapper;
    
    /**
     * 用户数据访问层依赖注入
     * 用于验证用户信息和权限
     */
    @Resource
    private UserMapper userMapper;

    /**
     * 借阅图书
     * 
     * 用户借阅图书的核心业务逻辑，包含完整的验证和状态管理。
     * 
     * @param bookBorrowRequestDto 借阅请求DTO，包含图书ID和用户ID
     * @return boolean 借阅是否成功，true表示成功，false表示失败
     * @throws LibraryException 各种业务验证失败时抛出相应异常
     * 
     * 业务验证流程：
     * 1. 验证图书是否存在
     * 2. 验证用户是否存在且为读者角色
     * 3. 检查是否已借阅该图书（防止重复借阅）
     * 4. 验证图书库存是否充足
     * 5. 创建借阅记录
     * 6. 减少图书库存
     * 
     * 事务保证：
     * - 借阅记录创建和库存减少在同一事务中执行
     * - 任何一步失败都会回滚整个操作
     * - 确保数据一致性
     * 
     * 数据冗余设计：
     * - 在借阅记录中冗余存储ISBN和用户名
     * - 提高查询性能，避免频繁关联查询
     */
    @Override
    @Transactional  // 事务注解，确保操作的原子性
    public boolean borrowBook(BookBorrowRequestDto bookBorrowRequestDto) {
        Long bookId = bookBorrowRequestDto.getBookId();
        Long userId = bookBorrowRequestDto.getUserId();
        
        // 验证图书是否存在
        BookDto book = bookService.getBook(bookId);
        if (book == null) {
            throw new LibraryException(500, "图书不存在");
        }
        
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new LibraryException(500, "用户不存在");
        }
        
        // 验证用户角色是否为读者
        if (!isReader(user)) {
            throw new LibraryException(500, "当前用户不是读者用户");
        }
        
        // 检查是否已经借阅该图书（防止重复借阅）
        BorrowRecord originRecord = borrowRecordMapper.findOneByUserIdAndBookIdAndStatus(userId, bookId, STATUS_LEND);
        if (originRecord != null) {
            throw new LibraryException(500, "不允许重复借书");
        }
        
        // 验证图书库存是否充足
        if (book.getStock() <= 0) {
            throw new LibraryException(500, "图书库存不足");
        }
        
        // 创建借阅记录
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUserId(userId);
        borrowRecord.setBookId(bookId);
        borrowRecord.setBorrowDate(LocalDateTime.now());  // 设置借阅时间
        borrowRecord.setReturnDate(null);  // 归还时间初始为空
        borrowRecord.setStatus(STATUS_LEND);  // 设置为借出状态
        
        // 冗余存储，提高查询性能
        borrowRecord.setIsbn(book.getIsbn());
        borrowRecord.setUsername(user.getUsername());
        
        // 设置时间戳
        borrowRecord.setUpdatedAt(LocalDateTime.now());
        borrowRecord.setCreatedAt(LocalDateTime.now());
        
        // 保存借阅记录
        borrowRecordMapper.insert(borrowRecord);

        // 减少图书库存（使用乐观锁保证并发安全）
        return bookService.decreaseStock(bookId);
    }

    /**
     * 归还图书
     * 
     * 用户归还图书的业务逻辑，更新借阅记录状态并恢复库存。
     * 
     * @param bookReturnRequestDto 归还请求DTO，包含图书ID和用户ID
     * @return boolean 归还是否成功，true表示成功，false表示失败
     * @throws LibraryException 各种业务验证失败时抛出相应异常
     * 
     * 业务验证流程：
     * 1. 验证图书是否存在
     * 2. 验证用户是否存在且为读者角色
     * 3. 查找对应的借阅记录
     * 4. 更新借阅记录状态为已归还
     * 5. 增加图书库存
     * 
     * 特殊处理：
     * - 如果没有找到借阅记录，直接返回成功（幂等操作）
     * - 避免重复归还的问题
     * 
     * 事务保证：
     * - 记录更新和库存增加在同一事务中执行
     * - 确保数据一致性
     */
    @Override
    @Transactional  // 事务注解，确保操作的原子性
    public boolean returnBook(BookReturnRequestDto bookReturnRequestDto) {
        Long bookId = bookReturnRequestDto.getBookId();
        Long userId = bookReturnRequestDto.getUserId();
        
        // 验证图书是否存在
        BookDto book = bookService.getBook(bookId);
        if (book == null) {
            throw new LibraryException(500, "图书不存在");
        }
        
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new LibraryException(500, "用户不存在");
        }
        
        // 验证用户角色是否为读者
        if (!isReader(user)) {
            throw new LibraryException(500, "当前用户不是读者用户");
        }
        
        // 查找对应的借阅记录
        BorrowRecord originRecord = borrowRecordMapper.findOneByUserIdAndBookIdAndStatus(userId, bookId, STATUS_LEND);
        if (originRecord == null) {
            // 无借阅记录直接返回成功（幂等操作）
            return true;
        }
        
        // 更新借阅记录状态
        originRecord.setReturnDate(LocalDateTime.now());  // 设置归还时间
        originRecord.setStatus(LibraryConstants.STATUS_RETURN);  // 更新为已归还状态
        originRecord.setUpdatedAt(LocalDateTime.now());  // 更新修改时间
        borrowRecordMapper.updateById(originRecord);
        
        // 增加图书库存（使用乐观锁保证并发安全）
        return bookService.increaseStock(bookId);
    }

    /**
     * 查找用户借阅的图书
     * 
     * 根据图书ID列表和用户ID，查找用户当前借阅中的图书记录。
     * 
     * @param bookIds 图书ID列表
     * @param userId 用户ID
     * @return List<BorrowRecord> 借阅记录列表，如果没有则返回空列表
     * 
     * 应用场景：
     * - 检查用户是否已借阅特定图书
     * - 获取用户的借阅状态信息
     * - 批量查询借阅情况
     * 
     * 性能优化：
     * - 使用批量查询，避免多次数据库访问
     * - 只查询借出状态的记录，过滤已归还的记录
     */
    @Override
    public List<BorrowRecord> findBorrowBooks(List<Long> bookIds, Long userId) {
        // 批量查询用户的借出记录
        List<BorrowRecord> borrowBooks = borrowRecordMapper.findAllByBookIdInAndStatusAndUserId(bookIds, STATUS_LEND, userId);
        
        // 检查查询结果
        if (CollectionUtils.isEmpty(borrowBooks)) {
            return Collections.emptyList();
        }
        
        return borrowBooks;
    }

    /**
     * 获取用户借阅图书信息
     * 
     * 获取用户的借阅统计信息，包括借阅数量和逾期图书列表。
     * 
     * @param userId 用户ID
     * @return UserBorrowBooksInfoDto 用户借阅信息DTO，包含借阅数量和逾期图书
     * 
     * 统计信息：
     * - 当前借阅图书总数
     * - 逾期图书列表（借阅超过30天的图书）
     * - 每本逾期图书的详细信息
     * 
     * 逾期计算规则：
     * - 借阅时间 + 30天 < 当前时间 = 逾期
     * - 只统计未归还的图书
     * 
     * 性能优化：
     * - 使用Stream API进行数据过滤和转换
     * - 批量查询图书信息，避免N+1查询问题
     * - 使用Map进行数据关联，提高查找效率
     */
    @Override
    public UserBorrowBooksInfoDto getUserBorrowBooksInfo(Long userId) {
        // 查询用户所有借出状态的记录
        List<BorrowRecord> borrowRecords = borrowRecordMapper.findAllByUserIdAndStatus(userId, STATUS_LEND);
        
        // 如果没有借阅记录，返回空结果
        if (CollectionUtils.isEmpty(borrowRecords)) {
            UserBorrowBooksInfoDto userBorrowBooksInfoDto = new UserBorrowBooksInfoDto();
            userBorrowBooksInfoDto.setBorrowNum(0);
            userBorrowBooksInfoDto.setDelayedBooks(Collections.emptyList());
            return userBorrowBooksInfoDto;
        }
        
        // 设置借阅总数
        UserBorrowBooksInfoDto userBorrowBooksInfoDto = new UserBorrowBooksInfoDto();
        userBorrowBooksInfoDto.setBorrowNum(borrowRecords.size());
        
        // 筛选逾期记录（借阅时间 + 30天 < 当前时间）
        List<BorrowRecord> delayRecords = borrowRecords.stream()
                .filter(borrowRecord -> borrowRecord.getBorrowDate().plusDays(30).isBefore(LocalDateTime.now()))
                .toList();
        
        // 如果没有逾期记录，返回空逾期列表
        if (CollectionUtils.isEmpty(delayRecords)) {
            userBorrowBooksInfoDto.setDelayedBooks(Collections.emptyList());
            return userBorrowBooksInfoDto;
        }
        
        // 批量查询逾期图书的详细信息
        List<BookDto> delayBooks = bookService.findAllByIdIn(
                delayRecords.stream().map(BorrowRecord::getBookId).collect(Collectors.toList()));
        
        // 构建图书ID到图书信息的映射，提高查找效率
        Map<Long, BookDto> bookDtoMap = delayBooks.stream()
                .collect(Collectors.toMap(BookDto::getId, book -> book));
        
        // 构建逾期图书的详细信息列表
        List<UserBorrowBookDto> ret = new ArrayList<>();
        for (BorrowRecord delayRecord : delayRecords) {
            UserBorrowBookDto userBorrowBookDto = toUserBorrowBookDto(delayRecord, bookDtoMap);
            ret.add(userBorrowBookDto);
        }
        
        userBorrowBooksInfoDto.setDelayedBooks(ret);
        return userBorrowBooksInfoDto;
    }

    /**
     * 续借图书
     * 
     * 用户对已借阅的图书进行续借，延长借阅期限。
     * 
     * @param bookBorrowRequestDto 续借请求DTO，包含图书ID和用户ID
     * @return boolean 续借是否成功，true表示成功，false表示失败
     * @throws LibraryException 各种业务验证失败时抛出相应异常
     * 
     * 续借规则：
     * - 只能对当前借出状态的图书进行续借
     * - 续借后借阅时间重新计算（从续借时间开始计算30天）
     * - 增加续借次数计数
     * - 不影响图书库存（库存保持不变）
     * 
     * 业务验证流程：
     * 1. 验证图书是否存在
     * 2. 验证用户是否存在且为读者角色
     * 3. 查找对应的借阅记录
     * 4. 更新借阅时间和续借次数
     * 
     * 特殊处理：
     * - 如果没有找到借阅记录，直接返回成功
     * - 避免对未借阅图书的续借操作
     */
    @Override
    public boolean renewBook(BookBorrowRequestDto bookBorrowRequestDto) {
        Long bookId = bookBorrowRequestDto.getBookId();
        Long userId = bookBorrowRequestDto.getUserId();
        
        // 验证图书是否存在
        BookDto book = bookService.getBook(bookId);
        if (book == null) {
            throw new LibraryException(500, "图书不存在");
        }
        
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new LibraryException(500, "用户不存在");
        }
        
        // 验证用户角色是否为读者
        if (!isReader(user)) {
            throw new LibraryException(500, "当前用户不是读者用户");
        }
        
        // 查找对应的借阅记录
        BorrowRecord originRecord = borrowRecordMapper.findOneByUserIdAndBookIdAndStatus(userId, bookId, STATUS_LEND);
        if (originRecord == null) {
            // 无借阅记录直接返回成功
            return true;
        }
        
        // 更新借阅记录
        originRecord.setBorrowDate(LocalDateTime.now());  // 重新设置借阅时间
        originRecord.setRenewTimes(originRecord.getRenewTimes() + 1);  // 增加续借次数
        originRecord.setUpdatedAt(LocalDateTime.now());  // 更新修改时间
        borrowRecordMapper.updateById(originRecord);
        
        return true;
    }

    /**
     * 借阅记录转用户借阅图书DTO的私有方法
     * 
     * 将借阅记录和图书信息组合转换为用户借阅图书DTO。
     * 用于构建包含完整信息的借阅数据。
     * 
     * @param delayRecord 借阅记录
     * @param bookDtoMap 图书ID到图书DTO的映射
     * @return UserBorrowBookDto 用户借阅图书DTO
     * 
     * 数据组合：
     * - 图书基本信息（标题、作者、出版社等）
     * - 借阅相关信息（借阅时间、到期时间、归还时间）
     * - 用户信息（用户ID）
     * 
     * 业务计算：
     * - 到期时间 = 借阅时间 + 30天
     * - 组合显示所需的所有字段
     */
    private UserBorrowBookDto toUserBorrowBookDto(BorrowRecord delayRecord, Map<Long, BookDto> bookDtoMap) {
        // 获取对应的图书信息
        BookDto bookDto = bookDtoMap.get(delayRecord.getBookId());
        
        // 创建用户借阅图书DTO
        UserBorrowBookDto userBorrowBookDto = new UserBorrowBookDto();
        
        // 设置图书基本信息
        userBorrowBookDto.setId(bookDto.getId());
        userBorrowBookDto.setTitle(bookDto.getTitle());
        userBorrowBookDto.setAuthor(bookDto.getAuthor());
        userBorrowBookDto.setPublisher(bookDto.getPublisher());
        userBorrowBookDto.setIsbn(bookDto.getIsbn());
        userBorrowBookDto.setPublishTime(bookDto.getPublishTime());
        userBorrowBookDto.setStock(bookDto.getStock());
        userBorrowBookDto.setPrice(bookDto.getPrice());
        userBorrowBookDto.setStatus(bookDto.getStatus());
        
        // 设置借阅相关信息
        userBorrowBookDto.setUserId(delayRecord.getUserId());
        userBorrowBookDto.setBorrowDate(delayRecord.getBorrowDate());
        userBorrowBookDto.setDeadDate(delayRecord.getBorrowDate().plusDays(30));  // 计算到期时间
        userBorrowBookDto.setReturnDate(delayRecord.getReturnDate());
        
        return userBorrowBookDto;
    }
}
