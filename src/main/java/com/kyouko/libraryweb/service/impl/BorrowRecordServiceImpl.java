package com.kyouko.libraryweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kyouko.libraryweb.domain.Book;
import com.kyouko.libraryweb.domain.BorrowRecord;
import com.kyouko.libraryweb.domain.User;
import com.kyouko.libraryweb.dto.BorrowRecordDto;
import com.kyouko.libraryweb.mapper.BookMapper;
import com.kyouko.libraryweb.mapper.BorrowRecordMapper;
import com.kyouko.libraryweb.mapper.UserMapper;
import com.kyouko.libraryweb.service.BorrowRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kyouko.libraryweb.common.LibraryConstants.MAX_RENEW_TIMES;

/**
 * 借阅记录服务实现类
 * 
 * 实现BorrowRecordService接口，提供借阅记录查询和管理的业务逻辑。
 * 
 * 主要功能：
 * - 借阅记录的分页查询和条件搜索
 * - 多维度搜索支持（用户名、ISBN、用户ID）
 * - 借阅记录与图书、用户信息的关联查询
 * - 数据传输对象(DTO)的组装和转换
 * - 续借次数的计算和显示
 * 
 * 查询特性：
 * - 支持多条件组合查询
 * - 批量关联查询优化性能
 * - 使用Map进行数据关联，避免N+1查询问题
 * - 按更新时间降序排列，最新记录优先显示
 * 
 * 数据整合：
 * - 将借阅记录、图书信息、用户信息整合为完整的DTO
 * - 计算剩余续借次数
 * - 计算借阅到期时间
 * - 提供丰富的查询维度
 * 
 * 性能优化：
 * - 使用MyBatis Plus的分页插件进行数据库层分页
 * - 批量查询关联数据，减少数据库访问次数
 * - 使用Stream API和Function进行高效的数据转换
 * 
 * @author kyouko
 * @version 1.0
 */
@Service  // 标识为Spring服务层组件
public class BorrowRecordServiceImpl implements BorrowRecordService {

    /**
     * 借阅记录数据访问层依赖注入
     * 用于执行借阅记录相关的数据库操作
     */
    @Resource
    private BorrowRecordMapper borrowRecordMapper;

    /**
     * 图书数据访问层依赖注入
     * 用于查询图书详细信息
     */
    @Resource
    private BookMapper bookMapper;

    /**
     * 用户数据访问层依赖注入
     * 用于查询用户详细信息
     */
    @Resource
    private UserMapper userMapper;

    /**
     * 分页搜索借阅记录
     * 
     * 支持按用户名、ISBN、用户ID进行搜索的分页查询。
     * 返回包含完整信息的借阅记录DTO，包括图书和用户的详细信息。
     * 
     * @param pageNum 页码（从1开始），null或小于1时默认为1
     * @param pageSize 每页大小，null或小于1时默认为10
     * @param username 用户名搜索关键词（可选，支持模糊搜索）
     * @param isbn ISBN搜索关键词（可选，支持模糊搜索）
     * @param userId 用户ID（可选，精确匹配）
     * @return IPage<BorrowRecordDto> 分页结果，包含借阅记录列表和分页信息
     * 
     * 查询特性：
     * - 支持多条件组合查询，条件之间为AND关系
     * - 按更新时间降序排列，最新记录排在前面
     * - 用户名和ISBN支持模糊搜索，用户ID为精确匹配
     * - 自动处理分页参数的边界情况
     * 
     * 性能优化策略：
     * 1. 使用MyBatis Plus的分页插件，在数据库层面进行分页
     * 2. 先查询借阅记录分页数据，再批量查询关联信息
     * 3. 使用Map进行数据关联，避免嵌套循环查询
     * 4. 只在有记录时才执行关联查询，避免无效查询
     * 
     * 数据整合流程：
     * 1. 根据条件分页查询借阅记录
     * 2. 提取记录中的图书ID和用户ID
     * 3. 批量查询图书和用户信息
     * 4. 构建ID到对象的映射关系
     * 5. 组装完整的BorrowRecordDto对象
     */
    @Override
    public IPage<BorrowRecordDto> searchRecords(Integer pageNum, Integer pageSize, String username, String isbn, Long userId) {
        // 参数校验和默认值设置
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        // 构建查询条件
        LambdaQueryWrapper<BorrowRecord> wrappers = Wrappers.lambdaQuery();
        wrappers.orderByDesc(BorrowRecord::getUpdatedAt);  // 按更新时间降序排列
        
        // 按用户名模糊搜索（如果提供了搜索关键词）
        if (StringUtils.hasText(username)) {
            wrappers.like(BorrowRecord::getUsername, username);
        }
        
        // 按ISBN模糊搜索（如果提供了搜索关键词）
        if (StringUtils.hasText(isbn)) {
            wrappers.like(BorrowRecord::getIsbn, isbn);
        }
        
        // 按用户ID精确匹配（如果提供了用户ID）
        if (Objects.nonNull(userId)) {
            wrappers.eq(BorrowRecord::getUserId, userId);
        }
        
        // 执行分页查询
        Page<BorrowRecord> borrowRecordPage = borrowRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrappers);
        if (borrowRecordPage == null) {
            return null;
        }
        
        // 获取当前页的借阅记录
        List<BorrowRecord> records = borrowRecordPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new Page<>();  // 返回空的分页对象
        }
        
        // 批量查询关联的图书信息
        List<Long> bookIds = records.stream().map(BorrowRecord::getBookId).collect(Collectors.toList());
        List<Book> books = bookMapper.selectBatchIds(bookIds);
        // 构建图书ID到图书对象的映射，提高查找效率
        Map<Long, Book> bookMap = books.stream().collect(Collectors.toMap(Book::getId, Function.identity()));

        // 批量查询关联的用户信息
        List<Long> userIds = records.stream().map(BorrowRecord::getUserId).toList();
        List<User> users = userMapper.selectBatchIds(userIds);
        // 构建用户ID到用户对象的映射，提高查找效率
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        
        // 将实体分页结果转换为DTO分页结果
        // 使用Lambda表达式将每个借阅记录转换为DTO
        return borrowRecordPage.convert(borrowRecord -> toBorrowRecordDto(borrowRecord, bookMap, userMap));
    }

    /**
     * 借阅记录转DTO的私有方法
     * 
     * 将BorrowRecord实体对象与关联的图书、用户信息组合转换为BorrowRecordDto。
     * 
     * @param borrowRecord 借阅记录实体对象
     * @param bookMap 图书ID到图书对象的映射
     * @param userMap 用户ID到用户对象的映射
     * @return BorrowRecordDto 借阅记录DTO对象
     * 
     * 数据组装说明：
     * - 从借阅记录中获取基本信息（ISBN、借阅时间、归还时间、状态）
     * - 从图书映射中获取图书标题
     * - 从用户映射中获取用户昵称
     * - 计算到期时间（借阅时间 + 30天）
     * - 计算剩余续借次数（最大续借次数 - 已续借次数）
     * 
     * 业务计算：
     * - 到期时间 = 借阅时间 + 30天（固定借阅期限）
     * - 剩余续借次数 = MAX_RENEW_TIMES - 已续借次数
     * - 用户名从借阅记录获取（冗余存储）
     * - 用户昵称从用户信息获取（用于显示）
     * 
     * 注意事项：
     * - 假设bookMap和userMap中一定包含对应的键值
     * - 如果映射中缺少对应数据，会抛出空指针异常
     * - 续借次数的计算基于系统常量MAX_RENEW_TIMES
     */
    private BorrowRecordDto toBorrowRecordDto(BorrowRecord borrowRecord, Map<Long, Book> bookMap, Map<Long, User> userMap) {
        BorrowRecordDto borrowRecordDto = new BorrowRecordDto();
        
        // 设置基本借阅信息
        borrowRecordDto.setIsbn(borrowRecord.getIsbn());
        borrowRecordDto.setTitle(bookMap.get(borrowRecord.getBookId()).getTitle());  // 从图书信息获取标题
        borrowRecordDto.setUsername(borrowRecord.getUsername());  // 从借阅记录获取用户名（冗余存储）
        borrowRecordDto.setNickName(userMap.get(borrowRecord.getUserId()).getNickName());  // 从用户信息获取昵称
        
        // 设置时间相关信息
        borrowRecordDto.setBorrowDate(borrowRecord.getBorrowDate());
        borrowRecordDto.setReturnDate(borrowRecord.getReturnDate());
        borrowRecordDto.setDeadDate(borrowRecord.getBorrowDate().plusDays(30));  // 计算到期时间（借阅时间 + 30天）
        
        // 设置状态和续借信息
        borrowRecordDto.setStatus(borrowRecord.getStatus());
        borrowRecordDto.setRenewTimes(MAX_RENEW_TIMES - borrowRecord.getRenewTimes());  // 计算剩余续借次数
        
        // 设置关联ID，用于前端操作
        borrowRecordDto.setBookId(borrowRecord.getBookId());
        borrowRecordDto.setUserId(borrowRecord.getUserId());
        
        return borrowRecordDto;
    }
}
