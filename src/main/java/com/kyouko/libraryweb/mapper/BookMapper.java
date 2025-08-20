package com.kyouko.libraryweb.mapper;

import com.kyouko.libraryweb.domain.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author kyouko
* @description 针对表【book】的数据库操作Mapper
* @createDate 2025-08-19 17:47:29
* @Entity com.kyouko.libraryweb.domain.Book
*/
public interface BookMapper extends BaseMapper<Book> {
    int decreaseStock(@Param("id") Long id, @Param("version") Integer version);

    int increaseStock(@Param("id") Long id, @Param("version") Integer version);
}




