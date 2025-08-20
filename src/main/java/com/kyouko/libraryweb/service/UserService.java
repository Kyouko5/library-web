package com.kyouko.libraryweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kyouko.libraryweb.dto.*;

import java.util.List;

public interface UserService {

    void register(RegisterDto registerDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    UserInfoDto getUserByUsername(String username);

    IPage<UserInfoDto> searchUser(Integer pageNum, Integer pageSize, String name, String phone, String email);

    void updateUser(Long id, UserInfoDto userInfoDto);

    void deleteUser(Long id);

    void batchDelete(List<Long> ids);

    void updateUserPassword(UserPasswordChangeDto userPasswordChangeDto);
}
