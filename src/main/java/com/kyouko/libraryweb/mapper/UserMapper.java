package com.kyouko.libraryweb.mapper;
import org.apache.ibatis.annotations.Param;

import com.kyouko.libraryweb.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author kyouko
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-08-19 17:47:29
* @Entity com.kyouko.libraryweb.domain.User
*/
public interface UserMapper extends BaseMapper<User> {
    User findOneByUsername(@Param("username") String username);
}




