package com.kyouko.libraryweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kyouko.libraryweb.dto.BorrowRecordDto;

public interface BorrowRecordService {


    /**
     * 查询借阅记录
     *
     * @param pageNum  页码，从1开始计数
     * @param pageSize 每页显示的数量
     * @param username 用户名，用于模糊查询借阅记录的用户名
     * @param isbn     ISBN号，用于模糊查询借阅记录的ISBN号
     * @param userId   用户ID，用于精确查询指定用户的借阅记录
     * @return 返回一个IPage对象，包含分页信息和借阅记录列表（BorrowRecordDto类型）
     */
    IPage<BorrowRecordDto> searchRecords(Integer pageNum, Integer pageSize, String username, String isbn, Long userId);

}
