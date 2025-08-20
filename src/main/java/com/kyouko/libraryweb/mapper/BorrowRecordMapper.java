package com.kyouko.libraryweb.mapper;
import java.util.List;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;

import com.kyouko.libraryweb.domain.BorrowRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author kyouko
* @description 针对表【borrow_record】的数据库操作Mapper
* @createDate 2025-08-19 17:47:29
* @Entity com.kyouko.libraryweb.domain.BorrowRecord
*/
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
    BorrowRecord findOneByUserIdAndBookIdAndStatus(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("status") String status);

    List<BorrowRecord> findAllByBookIdInAndStatusAndUserId(@Param("bookIdList") Collection<Long> bookIdList, @Param("status") String status, @Param("userId") Long userId);

    List<BorrowRecord> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
}




