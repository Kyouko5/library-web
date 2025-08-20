package com.kyouko.libraryweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kyouko.libraryweb.common.PlainResult;
import com.kyouko.libraryweb.domain.BorrowRecord;
import com.kyouko.libraryweb.mapper.BookMapper;
import com.kyouko.libraryweb.mapper.BorrowRecordMapper;
import com.kyouko.libraryweb.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表盘统计控制器
 * 
 * 负责提供系统统计数据和图表数据的API接口。
 * 主要用于管理员后台的数据展示和系统监控。
 * 
 * 主要功能：
 * - 系统基础统计：用户数量、图书数量、借阅记录数量
 * - 趋势分析：最近一周的借阅趋势图表数据
 * - 实时数据：当前系统的运行状态统计
 * 
 * 数据特点：
 * - 实时计算统计数据，确保数据准确性
 * - 提供图表友好的数据格式
 * - 支持时间范围的趋势分析
 * 
 * 性能考虑：
 * - 使用MyBatis-Plus的count查询，性能较好
 * - 时间范围查询使用索引优化
 * - 数据量不大时可以考虑缓存优化
 * 
 * 权限要求：
 * - 通常需要管理员权限才能访问
 * - 包含系统敏感的统计信息
 * 
 * @author kyouko
 * @version 1.0
 */
@RestController  // 标识为REST控制器，自动将返回值序列化为JSON
@RequestMapping("/api/dashboard")  // 统一的API路径前缀
public class DashBoardController {

    /**
     * 用户数据访问层依赖注入
     * 用于查询用户相关的统计数据
     */
    @Resource
    private UserMapper userMapper;
    
    /**
     * 图书数据访问层依赖注入
     * 用于查询图书相关的统计数据
     */
    @Resource
    private BookMapper bookMapper;
    
    /**
     * 借阅记录数据访问层依赖注入
     * 用于查询借阅记录相关的统计数据
     */
    @Resource
    private BorrowRecordMapper borrowRecordMapper;

    /**
     * 获取系统统计数据接口
     * 
     * 返回系统的核心统计指标和趋势数据，用于仪表盘展示。
     * 包括基础统计数据和最近一周的借阅趋势图表数据。
     * 
     * @return PlainResult<Map<String, Object>> 包含统计数据的统一响应格式
     * 
     * API路径: GET /api/dashboard/stats
     * 
     * 响应数据结构:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "userCount": 150,           // 用户总数
     *     "lendRecordCount": 1200,    // 借阅记录总数
     *     "bookCount": 5000,          // 图书总数
     *     "dailyLendRecords": [       // 最近一周每日借阅数据
     *       {
     *         "date": "2024-01-01",
     *         "count": 15
     *       },
     *       {
     *         "date": "2024-01-02", 
     *         "count": 23
     *       },
     *       // ... 7天的数据
     *     ]
     *   }
     * }
     * 
     * 统计维度说明：
     * - userCount: 系统注册用户总数
     * - lendRecordCount: 历史借阅记录总数（包括已归还和未归还）
     * - bookCount: 图书馆藏书总数
     * - dailyLendRecords: 最近7天每天的新增借阅数量
     * 
     * 使用场景：
     * - 管理员仪表盘首页数据展示
     * - 系统运营状况监控
     * - 借阅趋势分析图表
     * - 系统健康状态评估
     * 
     * 性能优化：
     * - 使用count查询而非select *，提高查询效率
     * - 时间范围查询使用between优化
     * - 可以考虑定时任务预计算并缓存结果
     */
    @GetMapping("/stats")
    public PlainResult<Map<String, Object>> getStatistics() {
        // 查询用户总数
        // 使用QueryWrapper进行无条件查询，统计所有用户数量
        long userCount = userMapper.selectCount(new QueryWrapper<>());

        // 查询借阅记录总数
        // 统计系统中所有的借阅记录数量（包括已归还和未归还的记录）
        long lendRecordCount = borrowRecordMapper.selectCount(new QueryWrapper<>());

        // 查询图书总数
        // 统计图书馆的藏书总量
        long bookCount = bookMapper.selectCount(new QueryWrapper<>());

        // 构造基础统计数据的返回Map
        Map<String, Object> statsMap = new HashMap<>();
        statsMap.put("userCount", userCount);
        statsMap.put("lendRecordCount", lendRecordCount);
        statsMap.put("bookCount", bookCount);

        // 获取时间范围：当前日期和一周前的日期
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusWeeks(1);

        // 查询最近一周的借阅记录
        // 使用LambdaQueryWrapper构建类型安全的查询条件
        LambdaQueryWrapper<BorrowRecord> borrowRecordQuery = Wrappers.lambdaQuery();
        // between查询：borrowDate在[oneWeekAgo, today]范围内的记录
        borrowRecordQuery.between(BorrowRecord::getBorrowDate, oneWeekAgo, today);
        List<BorrowRecord> borrowRecords = borrowRecordMapper.selectList(borrowRecordQuery);

        // 使用Stream API按日期统计借阅数量
        // 将借阅记录按借阅日期分组，并统计每组的数量
        Map<LocalDate, Long> borrowCountPerDay = borrowRecords.stream()
                .collect(Collectors.groupingBy(
                    borrowRecord -> borrowRecord.getBorrowDate().toLocalDate(), // 按日期分组
                    Collectors.counting()  // 统计每组的数量
                ));

        // 构造完整的一周数据，确保包含所有日期（即使某些日期没有借阅记录）
        List<Map<String, Object>> dailyLendRecords = new ArrayList<>();
        
        // 遍历一周内的每一天
        for (LocalDate date = oneWeekAgo; !date.isAfter(today); date = date.plusDays(1)) {
            Map<String, Object> record = new HashMap<>();
            record.put("date", date.toString());  // 日期字符串，格式：yyyy-MM-dd
            // 获取该日期的借阅数量，如果没有记录则默认为0
            record.put("count", borrowCountPerDay.getOrDefault(date, 0L));
            dailyLendRecords.add(record);
        }
        
        // 将每日借阅数据添加到返回结果中
        statsMap.put("dailyLendRecords", dailyLendRecords);

        return PlainResult.success(statsMap);
    }
}
