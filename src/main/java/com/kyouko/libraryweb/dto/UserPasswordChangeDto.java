package com.kyouko.libraryweb.dto;

import lombok.Data;

@Data
public class UserPasswordChangeDto {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}
